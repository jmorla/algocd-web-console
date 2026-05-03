package com.algocd.webportal.mql.tree;

/**
 * Represents an identifier in an expression.
 */
public final record Identifier(String name) implements Expression {
}
