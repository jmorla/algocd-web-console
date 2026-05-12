package com.algocd.webportal.mql;

import com.algocd.webportal.mql.tree.*;

import java.util.ArrayList;
import java.util.List;

public class Mql5Parser implements Parser {
    private final Lexer lexer;

    public Mql5Parser(Lexer lexer) {
        this.lexer = lexer;
    }

    public Mql5Parser(String content) {
        this.lexer = new MqlScanner(new MqlTokenizer(content.toCharArray()));
    }

    @Override
    public Statement[] parse() {
        List<Statement> statements = new ArrayList<>();
        while (true) {
            Result<Token, SyntaxError> result = lexer.getToken();
            if (!result.isSuccess()) {
                throw new SyntaxException(result.getError().message, result.getError().position);
            }
            
            Token token = result.getValue();
            if (token.kind == Token.TokenKind.EOF) {
                break;
            }

            if (token.kind == Token.TokenKind.PROPERTY) {
                statements.add(parseProperty());
            } else if (token.kind == Token.TokenKind.INPUT || token.kind == Token.TokenKind.EXTERN || token.kind == Token.TokenKind.STATIC) {
                Statement stmt = parseVariableDeclaration();
                if (stmt != null) {
                    statements.add(stmt);
                }
            } else {
                // For metadata extraction, we skip top-level tokens we don't recognize
                // like function definitions, class keywords, etc.
                lexer.nextToken();
            }
        }
        return statements.toArray(new Statement[0]);
    }

    private PropertyStatement parseProperty() {
        match(Token.TokenKind.PROPERTY);
        Token idToken = match(Token.TokenKind.IDENTIFIER);
        
        // Peek to see if there is an expression following
        Result<Token, SyntaxError> peekRes = lexer.getToken();
        if (peekRes.isSuccess()) {
            Token peek = peekRes.getValue();
            // In MQL5 properties, the value follows the identifier on the same line.
            if (peek.kind != Token.TokenKind.EOF && 
                peek.kind != Token.TokenKind.PROPERTY && 
                peek.kind != Token.TokenKind.INPUT && 
                peek.kind != Token.TokenKind.EXTERN &&
                peek.kind != Token.TokenKind.STATIC &&
                peek.kind != Token.TokenKind.SEMICOLON) {
                Expression value = parseExpression();
                return new PropertyStatement(((Token.IdentifierToken) idToken).name, value);
            }
        }
        
        return new PropertyStatement(((Token.IdentifierToken) idToken).name, null);
    }

    private VariableDeclaration parseVariableDeclaration() {
        Token first = lexer.getToken().getValue();
        if (first.kind == Token.TokenKind.STATIC) {
            lexer.nextToken();
        }

        Token modifierToken = lexer.getToken().getValue();
        if (modifierToken.kind != Token.TokenKind.INPUT && modifierToken.kind != Token.TokenKind.EXTERN) {
             // If it was just 'static' without input/extern, we skip it for now (could be a global var)
             return null;
        }
        
        VariableDeclaration.Modifier modifier = modifierToken.kind == Token.TokenKind.INPUT ? 
                VariableDeclaration.Modifier.INPUT : VariableDeclaration.Modifier.EXTERN;
        lexer.nextToken();

        // Check for 'group'
        Token next = lexer.getToken().getValue();
        if (next.kind == Token.TokenKind.GROUP) {
            lexer.nextToken(); // group
            lexer.nextToken(); // "label"
            // input group doesn't have a semicolon
            return null;
        }

        Token typeToken = lexer.getToken().getValue();
        lexer.nextToken();

        Token nameToken = match(Token.TokenKind.IDENTIFIER);
        
        Expression value = null;
        Result<Token, SyntaxError> nextRes = lexer.getToken();
        if (!nextRes.isSuccess()) throw new SyntaxException(nextRes.getError().message, nextRes.getError().position);
        
        if (nextRes.getValue().kind == Token.TokenKind.ASSIGN) {
            lexer.nextToken();
            value = parseExpression();
        }

        // Variable declarations should end with semicolon, but some might have comments
        if (lexer.getToken().getValue().kind == Token.TokenKind.SEMICOLON) {
            lexer.nextToken();
        }

        return new VariableDeclaration(modifier, typeToken.kind, ((Token.IdentifierToken) nameToken).name, value);
    }

    private Expression parseExpression() {
        Result<Token, SyntaxError> res = lexer.getToken();
        if (!res.isSuccess()) throw new SyntaxException(res.getError().message, res.getError().position);
        
        Token token = res.getValue();
        if (token.kind == Token.TokenKind.EOF || token.kind == Token.TokenKind.SEMICOLON) {
             throw new SyntaxException("Expected expression, found: " + token.kind, token.start);
        }
        
        lexer.nextToken();

        return switch (token) {
            case Token.StringLiteralToken str -> new Literal.StringLiteral(str.value);
            case Token.NumberLiteralToken num -> new Literal.NumberLiteral(num.value);
            case Token.BooleanLiteralToken bool -> new Literal.BooleanLiteral(bool.value);
            case Token.DateTimeLiteralToken dt -> new Literal.DateTimeLiteral(dt.value);
            case Token.IdentifierToken id -> new Identifier(id.name);
            default -> throw new SyntaxException("Expected expression, found: " + token.kind, token.start);
        };
    }

    private Token match(Token.TokenKind expectedKind) {
        Result<Token, SyntaxError> res = lexer.getToken();
        if (!res.isSuccess()) {
            throw new SyntaxException(res.getError().message, res.getError().position);
        }
        Token token = res.getValue();
        if (token.kind == expectedKind) {
            lexer.nextToken();
            return token;
        }
        throw new SyntaxException("Expected " + expectedKind + " but found " + token.kind, token.start);
    }
}
