package com.algocd.webportal.mql.tree;

/**
 * Base interface for all expressions in the MQL AST.
 */
public sealed interface Expression extends Node 
    permits Literal, Identifier {
}
