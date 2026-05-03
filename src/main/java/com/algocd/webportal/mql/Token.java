package com.algocd.webportal.mql;

public class Token {

    public final TokenKind kind;

    public final int start;

    public final int end;

    public Token(TokenKind kind, int start, int end) {
        this.kind = kind;
        this.start = start;
        this.end = end;
    }

    public static class IdentifierToken extends Token {
        public final String name;

        public IdentifierToken(int start, int end, String name) {
            super(TokenKind.IDENTIFIER, start, end);
            this.name = name;
        }

        @Override
        public String toString() {
            return String.format("IdentifierToken[%d-%d, name='%s']", start, end, name);
        }
    }

    public static class StringLiteralToken extends Token {
        public final String value;

        public StringLiteralToken(int start, int end, String value) {
            super(TokenKind.STRING_LITERAL, start, end);
            this.value = value;
        }

        @Override
        public String toString() {
            return String.format("StringLiteralToken[%d-%d, value='%s']", start, end, value);
        }
    }

    public static class NumberLiteralToken extends Token {
        public final String value;

        public NumberLiteralToken(int start, int end, String value) {
            super(TokenKind.NUMBER_LITERAL, start, end);
            this.value = value;
        }

        @Override
        public String toString() {
            return String.format("NumberLiteralToken[%d-%d, value='%s']", start, end, value);
        }
    }

    public static class BooleanLiteralToken extends Token {
        public final boolean value;

        public BooleanLiteralToken(int start, int end, boolean value) {
            super(TokenKind.BOOLEAN_LITERAL, start, end);
            this.value = value;
        }

        @Override
        public String toString() {
            return String.format("BooleanLiteralToken[%d-%d, value=%b]", start, end, value);
        }
    }

    @Override
    public String toString() {
        return String.format("Token[%s, %d-%d]", kind, start, end);
    }

    public enum TokenKind {
        // Keywords
        PROPERTY("#property"),
        INPUT("input"),
        EXTERN("extern"),
        
        // Data Types Keywords
        BOOL("bool"), 
        CHAR("char"), 
        UCHAR("uchar"), 
        SHORT("short"), 
        USHORT("ushort"), 
        INT("int"), 
        UINT("uint"), 
        LONG("long"), 
        ULONG("ulong"), 
        DOUBLE("double"), 
        FLOAT("float"), 
        STRING("string"), 
        COLOR("color"), 
        DATETIME("datetime"), 
        ENUM("enum"),
        
        // Literals
        IDENTIFIER("identifier"),
        STRING_LITERAL("string"),
        NUMBER_LITERAL("number"),
        BOOLEAN_LITERAL("boolean"),
        
        // Punctuation
        ASSIGN("="),
        SEMICOLON(";"),
        
        // Control
        EOF("eof"),
        UNKNOWN("unknown");

        public final String label;

        TokenKind(String label) {
            this.label = label;
        }
    }
}
