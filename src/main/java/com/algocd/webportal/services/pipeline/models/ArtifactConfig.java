package com.algocd.webportal.services.pipeline.models;

import java.util.Map;

public class ArtifactConfig {
    private String kind;
    private String version;
    private ArtifactMetadata metadata;
    private Map<String, ArtifactParameter> parameters;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public ArtifactMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(ArtifactMetadata metadata) {
        this.metadata = metadata;
    }

    public Map<String, ArtifactParameter> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, ArtifactParameter> parameters) {
        this.parameters = parameters;
    }
}
