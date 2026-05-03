package com.algocd.webportal.mql;

import com.algocd.webportal.mql.tree.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Mql5ParserTest {

    @Test
    void testParseProperty() {
        String code = "#property copyright \"AlgoCD\"";
        MqlTokenizer tokenizer = new MqlTokenizer(code.toCharArray());
        MqlScanner scanner = new MqlScanner(tokenizer);
        Mql5Parser parser = new Mql5Parser(scanner);

        Statement[] statements = parser.parse();
        assertEquals(1, statements.length);
        assertTrue(statements[0] instanceof PropertyStatement);
        
        PropertyStatement prop = (PropertyStatement) statements[0];
        assertEquals("copyright", prop.name());
        assertTrue(prop.value() instanceof Literal.StringLiteral);
        assertEquals("AlgoCD", ((Literal.StringLiteral) prop.value()).value());
    }

    @Test
    void testParseVariableDeclarations() {
        String code = "input int Period = 10; extern double Price = 1.5;";
        MqlTokenizer tokenizer = new MqlTokenizer(code.toCharArray());
        MqlScanner scanner = new MqlScanner(tokenizer);
        Mql5Parser parser = new Mql5Parser(scanner);

        Statement[] statements = parser.parse();
        assertEquals(2, statements.length);
        
        assertTrue(statements[0] instanceof VariableDeclaration);
        VariableDeclaration var1 = (VariableDeclaration) statements[0];
        assertEquals(VariableDeclaration.Modifier.INPUT, var1.modifier());
        assertEquals(Token.TokenKind.INT, var1.type());
        assertEquals("Period", var1.name());
        assertTrue(var1.value() instanceof Literal.NumberLiteral);
        assertEquals("10", ((Literal.NumberLiteral) var1.value()).value());

        assertTrue(statements[1] instanceof VariableDeclaration);
        VariableDeclaration var2 = (VariableDeclaration) statements[1];
        assertEquals(VariableDeclaration.Modifier.EXTERN, var2.modifier());
        assertEquals(Token.TokenKind.DOUBLE, var2.type());
        assertEquals("Price", var2.name());
        assertTrue(var2.value() instanceof Literal.NumberLiteral);
        assertEquals("1.5", ((Literal.NumberLiteral) var2.value()).value());
    }

    @Test
    void testSyntaxErrorPropagation() {
        String code = "input int Period = 10.5.5;";
        MqlTokenizer tokenizer = new MqlTokenizer(code.toCharArray());
        MqlScanner scanner = new MqlScanner(tokenizer);
        Mql5Parser parser = new Mql5Parser(scanner);

        SyntaxException ex = assertThrows(SyntaxException.class, parser::parse);
        assertTrue(ex.getMessage().contains("Malformed number"));
    }
    
    @Test
    void testUnexpectedTokenError() {
        String code = "input int Period = ;"; // Missing expression
        MqlTokenizer tokenizer = new MqlTokenizer(code.toCharArray());
        MqlScanner scanner = new MqlScanner(tokenizer);
        Mql5Parser parser = new Mql5Parser(scanner);

        SyntaxException ex = assertThrows(SyntaxException.class, parser::parse);
        assertTrue(ex.getMessage().contains("Expected expression"));
    }
}
