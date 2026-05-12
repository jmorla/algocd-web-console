package com.algocd.webportal.mql.tree;

/**
 * Represents a literal value in an expression.
 */
public sealed interface Literal extends Expression 
    permits Literal.StringLiteral, Literal.NumberLiteral, Literal.BooleanLiteral, Literal.DateTimeLiteral {
    
    record StringLiteral(String value) implements Literal {}
    record NumberLiteral(String value) implements Literal {}
    record BooleanLiteral(boolean value) implements Literal {}
    record DateTimeLiteral(String value) implements Literal {}
}
