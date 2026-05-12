package com.algocd.webportal.entities;

public enum ProcessingStatus {
    QUEUED,
    ANALYZING,
    ANALYZED,
    COMPILING,
    COMPILED,
    UPLOADING,
    COMPLETED,
    FAILED
}
