package com.algocd.webportal.entities;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Artifact {

    @NotNull
    private UUID artifactId;

    @NotBlank
    private String name;

    @NotBlank
    private String digest;

    @NotNull
    private ArtifactType type;

    @NotNull
    private Long sizeBytes;

    private Platform platform;

    private String version;

    private String filename;

    private Instant createdAt;

    public Artifact() {
    }

    public Artifact(UUID artifactId, String name, String digest, ArtifactType type, Long sizeBytes, Platform platform, String version, String filename, Instant createdAt) {
        this.artifactId = artifactId;
        this.name = name;
        this.digest = digest;
        this.type = type;
        this.sizeBytes = sizeBytes;
        this.platform = platform;
        this.version = version;
        this.filename = filename;
        this.createdAt = createdAt;
    }

    public UUID getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(UUID artifactId) {
        this.artifactId = artifactId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public ArtifactType getType() {
        return type;
    }

    public void setType(ArtifactType type) {
        this.type = type;
    }

    public Long getSizeBytes() {
        return sizeBytes;
    }

    public void setSizeBytes(Long sizeBytes) {
        this.sizeBytes = sizeBytes;
    }

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Artifact artifact = (Artifact) o;
        return Objects.equals(artifactId, artifact.artifactId) && Objects.equals(name, artifact.name) && Objects.equals(digest, artifact.digest) && type == artifact.type && Objects.equals(sizeBytes, artifact.sizeBytes) && platform == artifact.platform && Objects.equals(version, artifact.version) && Objects.equals(filename, artifact.filename) && Objects.equals(createdAt, artifact.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(artifactId, name, digest, type, sizeBytes, platform, version, filename, createdAt);
    }

    @Override
    public String toString() {
        return "Artifact{" +
                "artifactId=" + artifactId +
                ", name='" + name + '\'' +
                ", digest='" + digest + '\'' +
                ", type=" + type +
                ", sizeBytes=" + sizeBytes +
                ", platform=" + platform +
                ", version='" + version + '\'' +
                ", filename='" + filename + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
