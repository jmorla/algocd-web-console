package com.algocd.webportal.mql;

import com.algocd.webportal.mql.tree.*;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

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
        assertInstanceOf(PropertyStatement.class, statements[0]);
        
        PropertyStatement prop = (PropertyStatement) statements[0];
        assertEquals("copyright", prop.name());
        assertInstanceOf(Literal.StringLiteral.class, prop.value());
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

        assertInstanceOf(VariableDeclaration.class, statements[0]);
        VariableDeclaration var1 = (VariableDeclaration) statements[0];
        assertEquals(VariableDeclaration.Modifier.INPUT, var1.modifier());
        assertEquals(Token.TokenKind.INT, var1.type());
        assertEquals("Period", var1.name());
        assertInstanceOf(Literal.NumberLiteral.class, var1.value());
        assertEquals("10", ((Literal.NumberLiteral) var1.value()).value());

        assertInstanceOf(VariableDeclaration.class, statements[1]);
        VariableDeclaration var2 = (VariableDeclaration) statements[1];
        assertEquals(VariableDeclaration.Modifier.EXTERN, var2.modifier());
        assertEquals(Token.TokenKind.DOUBLE, var2.type());
        assertEquals("Price", var2.name());
        assertInstanceOf(Literal.NumberLiteral.class, var2.value());
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

    @Test
    void testParseComplexMql5File() {
        String code = """
            //+------------------------------------------------------------------+
            //|                                                         test.mq5 |
            //|                                  Copyright 2026, MetaQuotes Ltd. |
            //|                                             https://www.mql5.com |
            //+------------------------------------------------------------------+
            #property library
            #property copyright "Copyright 2026, MetaQuotes Ltd."
            #property link      "https://www.mql5.com"
            #property version   "1.00"
            input int variable = 10;
            //+------------------------------------------------------------------+
            //| My function                                                      |
            //+------------------------------------------------------------------+
            int MyCalculator(int value,int value2) export
               {
                return(value+value2);
               }
            //+------------------------------------------------------------------+
            """;
        Mql5Parser parser = new Mql5Parser(code);
        Statement[] statements = parser.parse();

        // Verify we captured the properties and inputs
        assertTrue(statements.length >= 5, "Should have parsed at least the 4 properties and 1 input");
        
        long propertiesCount = Arrays.stream(statements)
                .filter(s -> s instanceof PropertyStatement)
                .count();
        assertEquals(4, propertiesCount);

        long inputsCount = Arrays.stream(statements)
                .filter(s -> s instanceof VariableDeclaration)
                .count();
        assertEquals(1, inputsCount);
    }
}
