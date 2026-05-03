package com.algocd.webportal.mql;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MqlScannerTest {

    @Test
    void testBasicScanning() {
        String code = "input int Period = 10;";
        MqlTokenizer tokenizer = new MqlTokenizer(code.toCharArray());
        MqlScanner scanner = new MqlScanner(tokenizer);

        assertEquals(Token.TokenKind.INPUT, scanner.getToken().getValue().kind);
        scanner.nextToken();
        assertEquals(Token.TokenKind.INT, scanner.getToken().getValue().kind);
        scanner.nextToken();
        assertEquals("Period", ((Token.IdentifierToken)scanner.getToken().getValue()).name);
        scanner.nextToken();
        assertEquals(Token.TokenKind.ASSIGN, scanner.getToken().getValue().kind);
        scanner.nextToken();
        assertEquals("10", ((Token.NumberLiteralToken)scanner.getToken().getValue()).value);
        scanner.nextToken();
        assertEquals(Token.TokenKind.SEMICOLON, scanner.getToken().getValue().kind);
        scanner.nextToken();
        assertEquals(Token.TokenKind.EOF, scanner.getToken().getValue().kind);
        
        // Ensure calling nextToken at EOF doesn't crash or advance
        scanner.nextToken();
        assertEquals(Token.TokenKind.EOF, scanner.getToken().getValue().kind);
    }

    @Test
    void testLookahead() {
        String code = "input int Period = 10;";
        MqlTokenizer tokenizer = new MqlTokenizer(code.toCharArray());
        MqlScanner scanner = new MqlScanner(tokenizer);

        // Peek ahead
        assertEquals(Token.TokenKind.INT, scanner.getToken(1).getValue().kind);
        assertEquals("Period", ((Token.IdentifierToken)scanner.getToken(2).getValue()).name);
        assertEquals(Token.TokenKind.INPUT, scanner.getToken(0).getValue().kind);

        // Advance and peek
        scanner.nextToken();
        assertEquals(Token.TokenKind.INT, scanner.getToken(0).getValue().kind);
        assertEquals(Token.TokenKind.ASSIGN, scanner.getToken(2).getValue().kind);
    }

    @Test
    void testErrorPropagation() {
        String code = "input double Price = 10.5.5;";
        MqlTokenizer tokenizer = new MqlTokenizer(code.toCharArray());
        MqlScanner scanner = new MqlScanner(tokenizer);

        // skip: input, double, Price, =
        for(int i = 0; i < 4; i++) scanner.nextToken();

        Result<Token, SyntaxError> result = scanner.getToken();
        assertFalse(result.isSuccess());
        assertEquals("Malformed number: multiple decimal points", result.getError().message);

        // Ensure we can't advance past error
        scanner.nextToken();
        assertFalse(scanner.getToken().isSuccess());
        assertEquals("Malformed number: multiple decimal points", scanner.getToken().getError().message);
    }

    @Test
    void testPeekPastEOF() {
        String code = "input int X = 1;";
        MqlTokenizer tokenizer = new MqlTokenizer(code.toCharArray());
        MqlScanner scanner = new MqlScanner(tokenizer);

        // Peek way ahead
        Result<Token, SyntaxError> result = scanner.getToken(100);
        assertTrue(result.isSuccess());
        assertEquals(Token.TokenKind.EOF, result.getValue().kind);
    }
}
