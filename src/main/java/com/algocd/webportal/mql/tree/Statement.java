package com.algocd.webportal.mql.tree;

/**
 * Base interface for all statements in the MQL AST.
 */
public sealed interface Statement extends Node 
    permits PropertyStatement, VariableDeclaration {
}
