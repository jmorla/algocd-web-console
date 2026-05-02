package com.algocd.webportal.exceptions;

public enum ErrorReason {
    INVALID_BOOTSTRAP_TOKEN("The provided bootstrap token is invalid or does not exist."),
    BOOTSTRAP_TOKEN_EXPIRED("The provided bootstrap token has expired."),
    VALIDATION_FAILED("Validation failed for the request."),
    INTERNAL_SERVER_ERROR("An unexpected internal error occurred.");

    private final String defaultMessage;

    ErrorReason(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}
