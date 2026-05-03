package com.algocd.webportal.mql;

/**
 * Custom runtime exception for reporting syntax errors during parsing.
 */
public class SyntaxException extends RuntimeException {
    public final String message;
    public final int position;

    public SyntaxException(String message, int position) {
        super(String.format("Syntax error at %d: %s", position, message));
        this.message = message;
        this.position = position;
    }
}
