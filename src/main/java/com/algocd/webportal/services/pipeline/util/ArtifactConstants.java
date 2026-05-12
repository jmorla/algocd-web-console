package com.algocd.webportal.services.pipeline.util;

/**
 * Constants for the MQL5 artifact pipeline and OCI registry.
 */
public final class ArtifactConstants {

    private ArtifactConstants() {
        // Utility class
    }

    public static final String MQL5_CONFIG_MEDIA_TYPE = "application/vnd.mql5.config.v1+json";
    public static final String MQL5_EX5_LAYER_MEDIA_TYPE = "application/vnd.mql5.ex5.layer.v1+binary";
    public static final String MQL5_ARTIFACT_TYPE_BASE = "application/vnd.mql5.";
    
    public static final String OCI_IMAGE_TITLE = "org.opencontainers.image.title";
    public static final String OCI_IMAGE_CREATED = "org.opencontainers.image.created";
    
    public static final String CONFIG_EXTENSION = ".config.json";
    public static final String EX5_EXTENSION = ".ex5";
}
