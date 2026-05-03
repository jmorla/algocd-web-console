package com.algocd.webportal.mql.tree;

import com.algocd.webportal.mql.Token.TokenKind;

/**
 * Represents an input or extern variable declaration.
 * Example: input int Period = 10;
 */
public final record VariableDeclaration(
    Modifier modifier,
    TokenKind type,
    String name,
    Expression value
) implements Statement {

    public enum Modifier {
        INPUT, EXTERN
    }
}
