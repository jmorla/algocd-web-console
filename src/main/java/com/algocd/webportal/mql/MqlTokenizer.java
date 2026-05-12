package com.algocd.webportal.mql;

import java.util.HashMap;
import java.util.Map;

import com.algocd.webportal.mql.Token.TokenKind;

public class MqlTokenizer extends BaseReader {

    private enum State {
        SEEKING, IN_PROPERTY, IN_INPUT
    }

    private State state = State.SEEKING;

    /**
     * Keyword array. Maps name indices to Token.
     */
    private static final Map<String, TokenKind> keywords = new HashMap<>();

    static {
        keywords.put("input", TokenKind.INPUT);
        keywords.put("extern", TokenKind.EXTERN);
        keywords.put("group", TokenKind.GROUP);
        keywords.put("static", TokenKind.STATIC);
        keywords.put("bool", TokenKind.BOOL);
        keywords.put("char", TokenKind.CHAR);
        keywords.put("uchar", TokenKind.UCHAR);
        keywords.put("short", TokenKind.SHORT);
        keywords.put("ushort", TokenKind.USHORT);
        keywords.put("int", TokenKind.INT);
        keywords.put("uint", TokenKind.UINT);
        keywords.put("long", TokenKind.LONG);
        keywords.put("ulong", TokenKind.ULONG);
        keywords.put("double", TokenKind.DOUBLE);
        keywords.put("float", TokenKind.FLOAT);
        keywords.put("string", TokenKind.STRING);
        keywords.put("color", TokenKind.COLOR);
        keywords.put("datetime", TokenKind.DATETIME);
        keywords.put("enum", TokenKind.ENUM);
    }

    /**
     * Constructor.
     *
     * @param array
     *
     */
    public MqlTokenizer(char[] array) {
        super(array);
    }

    public Result<Token, SyntaxError> readToken() {
        scan: while (true) {
            skipWhitespaceAndComments();
            
            int start = position;
            int cp = codepoint;
            
            if (cp == EOF) {
                return Result.ok(new Token(TokenKind.EOF, start, start));
            }

            switch (state) {
                case SEEKING -> {
                    if (cp == '#') {
                        String id = readIdentifier();
                        if ("#property".equals(id)) {
                            state = State.IN_PROPERTY;
                            return Result.ok(new Token(TokenKind.PROPERTY, start, position));
                        }
                    } else if ((cp == 'D' || cp == 'd') && peek() == '\'') {
                        String val = readDateTime();
                        return Result.ok(new Token.DateTimeLiteralToken(start, position, val));
                    } else if (Character.isJavaIdentifierStart(cp)) {
                        String id = readIdentifier();
                        TokenKind k = keywords.get(id);
                        if (k == TokenKind.INPUT || k == TokenKind.EXTERN) {
                            state = State.IN_INPUT;
                            return Result.ok(new Token(k, start, position));
                        } else if (k != null) {
                            return Result.ok(new Token(k, start, position));
                        }
                        return Result.ok(new Token.IdentifierToken(start, position, id));
                    } else if (cp == '"') {
                        Result<String, SyntaxError> res = readString();
                        if (!res.isSuccess()) return Result.fail(res.getError());
                        return Result.ok(new Token.StringLiteralToken(start, position, res.getValue()));
                    } else if (Character.isDigit(cp)) {
                        Result<String, SyntaxError> res = readNumber();
                        if (!res.isSuccess()) return Result.fail(res.getError());
                        return Result.ok(new Token.NumberLiteralToken(start, position, res.getValue()));
                    } else {
                        nextCodePoint();
                    }
                }
                case IN_PROPERTY -> {
                    if (cp == '\n' || cp == '\r') {
                        state = State.SEEKING;
                        continue scan;
                    }
                    if (Character.isJavaIdentifierStart(cp)) {
                        String id = readIdentifier();
                        return Result.ok(new Token.IdentifierToken(start, position, id));
                    }
                    if ((cp == 'D' || cp == 'd') && peek() == '\'') {
                        String val = readDateTime();
                        return Result.ok(new Token.DateTimeLiteralToken(start, position, val));
                    }
                    if (Character.isDigit(cp)) {
                        Result<String, SyntaxError> res = readNumber();
                        if (!res.isSuccess()) return Result.fail(res.getError());
                        return Result.ok(new Token.NumberLiteralToken(start, position, res.getValue()));
                    }
                    if (cp == '"') {
                        Result<String, SyntaxError> res = readString();
                        if (!res.isSuccess()) return Result.fail(res.getError());
                        return Result.ok(new Token.StringLiteralToken(start, position, res.getValue()));
                    }
                    nextCodePoint();
                }
                case IN_INPUT -> {
                    if (cp == ';') {
                        state = State.SEEKING;
                        nextCodePoint();
                        return Result.ok(new Token(TokenKind.SEMICOLON, start, position));
                    }
                    if (Character.isJavaIdentifierStart(cp)) {
                        String id = readIdentifier();
                        TokenKind k = keywords.get(id);
                        if (k != null) {
                            if (k == TokenKind.GROUP) {
                                state = State.SEEKING; // input group doesn't end with ;
                            }
                            return Result.ok(new Token(k, start, position));
                        }
                        if ("true".equals(id) || "false".equals(id)) {
                            return Result.ok(new Token.BooleanLiteralToken(start, position, "true".equals(id)));
                        }
                        return Result.ok(new Token.IdentifierToken(start, position, id));
                    }
                    if ((cp == 'D' || cp == 'd') && peek() == '\'') {
                        String val = readDateTime();
                        return Result.ok(new Token.DateTimeLiteralToken(start, position, val));
                    }
                    if (Character.isDigit(cp)) {
                        Result<String, SyntaxError> res = readNumber();
                        if (!res.isSuccess()) return Result.fail(res.getError());
                        return Result.ok(new Token.NumberLiteralToken(start, position, res.getValue()));
                    }
                    if (cp == '"') {
                        Result<String, SyntaxError> res = readString();
                        if (!res.isSuccess()) return Result.fail(res.getError());
                        return Result.ok(new Token.StringLiteralToken(start, position, res.getValue()));
                    }
                    if (cp == '=') {
                        nextCodePoint();
                        return Result.ok(new Token(TokenKind.ASSIGN, start, position));
                    }
                    nextCodePoint();
                }
            }
        }
    }

    private void skipWhitespaceAndComments() {
        skip: while (true) {
            if (state == State.IN_PROPERTY && (codepoint == '\n' || codepoint == '\r')) {
                break skip;
            }
            if (Character.isWhitespace(codepoint)) {
                nextCodePoint();
                continue skip;
            }
            if (codepoint == '/') {
                int next = peek();
                if (next == '/') {
                    // Line comment
                    while (codepoint != '\n' && codepoint != '\r' && codepoint != EOF) {
                        nextCodePoint();
                    }
                    continue skip;
                } else if (next == '*') {
                    // Block comment
                    nextCodePoint(); // skip /
                    nextCodePoint(); // skip *
                    while (codepoint != EOF) {
                        if (codepoint == '*') {
                            if (peek() == '/') {
                                nextCodePoint(); // skip *
                                nextCodePoint(); // skip /
                                continue skip;
                            }
                        }
                        nextCodePoint();
                    }
                }
            }
            break skip;
        }
    }

    private String readIdentifier() {
        StringBuilder sb = new StringBuilder();
        if (codepoint == '#') {
            sb.append((char) codepoint);
            nextCodePoint();
        }
        while (Character.isJavaIdentifierPart(codepoint)) {
            sb.append((char) codepoint);
            nextCodePoint();
        }
        return sb.toString();
    }

    private String readDateTime() {
        StringBuilder sb = new StringBuilder();
        sb.append((char) codepoint);
        nextCodePoint(); // D or d
        sb.append((char) codepoint);
        nextCodePoint(); // '
        while (codepoint != '\'' && codepoint != EOF) {
            sb.append((char) codepoint);
            nextCodePoint();
        }
        if (codepoint == '\'') {
            sb.append((char) codepoint);
            nextCodePoint();
        }
        return sb.toString();
    }

    private Result<String, SyntaxError> readNumber() {
        StringBuilder sb = new StringBuilder();
        int dotCount = 0;
        SyntaxError error = null;
        while (Character.isDigit(codepoint) || codepoint == '.') {
            if (codepoint == '.') {
                dotCount++;
                if (dotCount > 1 && error == null) {
                    error = new SyntaxError("Malformed number: multiple decimal points", position);
                }
            }
            sb.append((char) codepoint);
            nextCodePoint();
        }
        return error != null ? Result.fail(error) : Result.ok(sb.toString());
    }

    private Result<String, SyntaxError> readString() {
        StringBuilder sb = new StringBuilder();
        int startPos = position;
        nextCodePoint(); // skip "
        while (codepoint != '"' && codepoint != EOF) {
            // Handle escaped quotes
            if (codepoint == '\\') {
                int next = peek();
                if (next == '"') {
                    sb.append('"');
                    nextCodePoint();
                    nextCodePoint();
                    continue;
                }
            }
            sb.append((char) codepoint);
            nextCodePoint();
        }
        if (codepoint == EOF) {
            return Result.fail(new SyntaxError("Unclosed string literal", startPos));
        }
        nextCodePoint(); // skip "
        return Result.ok(sb.toString());
    }

    private int peek() {
        if (position + 1 >= buffer.length) {
            return EOF;
        }
        return buffer[position + 1];
    }
}
