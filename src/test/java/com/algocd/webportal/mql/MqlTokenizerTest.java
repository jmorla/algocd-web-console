package com.algocd.webportal.mql;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MqlTokenizerTest {

    @Test
    void testParseProperty() {
        String code = "#property copyright \"AlgoCD\"";
        MqlTokenizer tokenizer = new MqlTokenizer(code.toCharArray());

        Result<Token, SyntaxError> result1 = tokenizer.readToken();
        assertTrue(result1.isSuccess());
        assertEquals(Token.TokenKind.PROPERTY, result1.getValue().kind);

        Result<Token, SyntaxError> result2 = tokenizer.readToken();
        assertTrue(result2.isSuccess());
        assertTrue(result2.getValue() instanceof Token.IdentifierToken);
        assertEquals("copyright", ((Token.IdentifierToken) result2.getValue()).name);

        Result<Token, SyntaxError> result3 = tokenizer.readToken();
        assertTrue(result3.isSuccess());
        assertTrue(result3.getValue() instanceof Token.StringLiteralToken);
        assertEquals("AlgoCD", ((Token.StringLiteralToken) result3.getValue()).value);
    }

    @Test
    void testParseInputVariable() {
        String code = "input int InpPeriod = 14; // The period";
        MqlTokenizer tokenizer = new MqlTokenizer(code.toCharArray());

        Result<Token, SyntaxError> result = tokenizer.readToken();
        assertTrue(result.isSuccess());
        assertEquals(Token.TokenKind.INPUT, result.getValue().kind);

        result = tokenizer.readToken();
        assertTrue(result.isSuccess());
        assertEquals(Token.TokenKind.INT, result.getValue().kind);

        result = tokenizer.readToken();
        assertTrue(result.isSuccess());
        assertTrue(result.getValue() instanceof Token.IdentifierToken);
        assertEquals("InpPeriod", ((Token.IdentifierToken) result.getValue()).name);

        result = tokenizer.readToken();
        assertTrue(result.isSuccess());
        assertEquals(Token.TokenKind.ASSIGN, result.getValue().kind);

        result = tokenizer.readToken();
        assertTrue(result.isSuccess());
        assertTrue(result.getValue() instanceof Token.NumberLiteralToken);
        assertEquals("14", ((Token.NumberLiteralToken) result.getValue()).value);

        result = tokenizer.readToken();
        assertTrue(result.isSuccess());
        assertEquals(Token.TokenKind.SEMICOLON, result.getValue().kind);

        // Next token should be EOF as comments are skipped
        result = tokenizer.readToken();
        assertTrue(result.isSuccess());
        assertEquals(Token.TokenKind.EOF, result.getValue().kind);
    }

    @Test
    void testParseOtherKeywordsAndCommentsSkipped() {
        String code = "void OnTick() { /* do something */ double x = 0.5; }";
        MqlTokenizer tokenizer = new MqlTokenizer(code.toCharArray());

        // Everything should be skipped as it's not part of #property, input, or extern
        Result<Token, SyntaxError> result = tokenizer.readToken();
        assertTrue(result.isSuccess());
        assertEquals(Token.TokenKind.EOF, result.getValue().kind);
    }

    @Test
    void testInterleavedDeclarations() {
        String code = "void OnTick() { }\n" +
                      "input int Period = 10;\n" +
                      "void somethingElse() { }\n" +
                      "extern double Price = 1.0;";
        MqlTokenizer tokenizer = new MqlTokenizer(code.toCharArray());

        assertEquals(Token.TokenKind.INPUT, tokenizer.readToken().getValue().kind);
        assertEquals(Token.TokenKind.INT, tokenizer.readToken().getValue().kind);
        assertEquals("Period", ((Token.IdentifierToken)tokenizer.readToken().getValue()).name);
        assertEquals(Token.TokenKind.ASSIGN, tokenizer.readToken().getValue().kind);
        assertEquals("10", ((Token.NumberLiteralToken)tokenizer.readToken().getValue()).value);
        assertEquals(Token.TokenKind.SEMICOLON, tokenizer.readToken().getValue().kind);

        assertEquals(Token.TokenKind.EXTERN, tokenizer.readToken().getValue().kind);
        assertEquals(Token.TokenKind.DOUBLE, tokenizer.readToken().getValue().kind);
        assertEquals("Price", ((Token.IdentifierToken)tokenizer.readToken().getValue()).name);
        assertEquals(Token.TokenKind.ASSIGN, tokenizer.readToken().getValue().kind);
        assertEquals("1.0", ((Token.NumberLiteralToken)tokenizer.readToken().getValue()).value);
        assertEquals(Token.TokenKind.SEMICOLON, tokenizer.readToken().getValue().kind);

        assertEquals(Token.TokenKind.EOF, tokenizer.readToken().getValue().kind);
    }

    @Test
    void testBooleansAndExtern() {
        String code = "extern bool flag = true;";
        MqlTokenizer tokenizer = new MqlTokenizer(code.toCharArray());

        Result<Token, SyntaxError> result = tokenizer.readToken();
        assertEquals(Token.TokenKind.EXTERN, result.getValue().kind);

        result = tokenizer.readToken();
        assertEquals(Token.TokenKind.BOOL, result.getValue().kind);

        result = tokenizer.readToken();
        assertEquals("flag", ((Token.IdentifierToken) result.getValue()).name);

        result = tokenizer.readToken();
        assertEquals(Token.TokenKind.ASSIGN, result.getValue().kind);

        result = tokenizer.readToken();
        assertTrue(((Token.BooleanLiteralToken) result.getValue()).value);

        result = tokenizer.readToken();
        assertEquals(Token.TokenKind.SEMICOLON, result.getValue().kind);
    }

    @Test
    void testUnclosedString() {
        String code = "input string name = \"AlgoCD;";
        MqlTokenizer tokenizer = new MqlTokenizer(code.toCharArray());

        tokenizer.readToken(); // input
        tokenizer.readToken(); // string
        tokenizer.readToken(); // name
        tokenizer.readToken(); // =

        Result<Token, SyntaxError> result = tokenizer.readToken();
        assertFalse(result.isSuccess());
        assertEquals("Unclosed string literal", result.getError().message);
    }

    @Test
    void testMalformedNumber() {
        String code = "input double price = 10.5.5;";
        MqlTokenizer tokenizer = new MqlTokenizer(code.toCharArray());

        tokenizer.readToken(); // input
        tokenizer.readToken(); // double
        tokenizer.readToken(); // price
        tokenizer.readToken(); // =

        Result<Token, SyntaxError> result = tokenizer.readToken();
        assertFalse(result.isSuccess());
        assertEquals("Malformed number: multiple decimal points", result.getError().message);
    }

    @Test
    void testInvalidIdentifierStartingWithDigit() {
        // MQL4/5 identifiers cannot start with a digit.
        // In our current implementation, if it starts with a digit, it's read as a number.
        // If it then contains letters, readNumber will stop at the first letter.
        // The letter will then be seeking for the next token.
        String code = "input int 10Period = 14;";
        MqlTokenizer tokenizer = new MqlTokenizer(code.toCharArray());

        assertEquals(Token.TokenKind.INPUT, tokenizer.readToken().getValue().kind);
        assertEquals(Token.TokenKind.INT, tokenizer.readToken().getValue().kind);
        
        // "10" is read as a number
        Token t3 = tokenizer.readToken().getValue();
        assertTrue(t3 instanceof Token.NumberLiteralToken);
        assertEquals("10", ((Token.NumberLiteralToken)t3).value);

        // "Period" is read as an identifier
        Token t4 = tokenizer.readToken().getValue();
        assertTrue(t4 instanceof Token.IdentifierToken);
        assertEquals("Period", ((Token.IdentifierToken)t4).name);
    }
}
