package com.algocd.webportal.mql;

public class SyntaxError {
    public final String message;
    public final int position;

    public SyntaxError(String message, int position) {
        this.message = message;
        this.position = position;
    }

    @Override
    public String toString() {
        return "SyntaxError{" +
                "message='" + message + '\'' +
                ", position=" + position +
                '}';
    }
}
