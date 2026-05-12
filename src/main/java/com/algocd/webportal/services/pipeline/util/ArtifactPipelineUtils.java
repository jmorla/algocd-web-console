package com.algocd.webportal.services.pipeline.util;

/**
 * Utility methods for the artifact pipeline.
 */
public final class ArtifactPipelineUtils {

    private ArtifactPipelineUtils() {
        // Utility class
    }

    /**
     * Replaces the file extension with an empty string.
     */
    public static String getBaseFilePath(String filePath) {
        return filePath.replaceAll("\\.[^.]*$", "");
    }

    /**
     * Normalizes a name for use in OCI annotations or paths (slugify).
     */
    public static String slugify(String name) {
        if (name == null) return "unknown";
        return name.toLowerCase().replaceAll("[^a-z0-9.]", "_");
    }

    /**
     * Builds the OCI artifact title annotation.
     */
    public static String buildArtifactTitle(String username, String kind, String slugName, String version) {
        return String.format("algocd/%s/%ss/%s/%s/ea.ex5", username, kind, slugName, version);
    }
}
