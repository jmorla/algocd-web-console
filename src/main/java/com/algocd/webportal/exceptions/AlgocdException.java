package com.algocd.webportal.exceptions;

public class AlgocdException extends RuntimeException {
    
    private final ErrorReason errorReason;

    public AlgocdException(ErrorReason errorReason) {
        super(errorReason.getDefaultMessage());
        this.errorReason = errorReason;
    }

    public AlgocdException(ErrorReason errorReason, String customMessage) {
        super(customMessage);
        this.errorReason = errorReason;
    }

    public AlgocdException(ErrorReason errorReason, Throwable cause) {
        super(errorReason.getDefaultMessage(), cause);
        this.errorReason = errorReason;
    }

    public AlgocdException(ErrorReason errorReason, String customMessage, Throwable cause) {
        super(customMessage, cause);
        this.errorReason = errorReason;
    }

    public ErrorReason getErrorCode() {
        return errorReason;
    }
}
