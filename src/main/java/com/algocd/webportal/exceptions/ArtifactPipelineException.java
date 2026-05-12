package com.algocd.webportal.exceptions;

import java.util.UUID;

/**
 * Exception thrown when an error occurs during the artifact processing pipeline.
 */
public class ArtifactPipelineException extends AlgocdException {

    private final UUID userId;
    private final String filename;

    public ArtifactPipelineException(ErrorReason errorReason, UUID userId, String filename) {
        super(errorReason);
        this.userId = userId;
        this.filename = filename;
    }

    public ArtifactPipelineException(ErrorReason errorReason, String customMessage, UUID userId, String filename) {
        super(errorReason, customMessage);
        this.userId = userId;
        this.filename = filename;
    }

    public ArtifactPipelineException(ErrorReason errorReason, String customMessage, Throwable cause, UUID userId, String filename) {
        super(errorReason, customMessage, cause);
        this.userId = userId;
        this.filename = filename;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getFilename() {
        return filename;
    }
}
