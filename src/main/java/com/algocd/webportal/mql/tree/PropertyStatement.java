package com.algocd.webportal.mql.tree;

/**
 * Represents a #property declaration.
 * Example: #property copyright "AlgoCD"
 */
public final record PropertyStatement(String name, Expression value) implements Statement {
}
