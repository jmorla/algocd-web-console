package com.algocd.webportal.entities;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Artifact {

    @NotNull
    private UUID artifactId;

    @NotNull
    private UUID userId;

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

    private Instant createdAt;

    public Artifact() {
    }

    public Artifact(UUID artifactId, UUID userId, String name, String digest, ArtifactType type, Long sizeBytes, Platform platform, String version, Instant createdAt) {
        this.artifactId = artifactId;
        this.userId = userId;
        this.name = name;
        this.digest = digest;
        this.type = type;
        this.sizeBytes = sizeBytes;
        this.platform = platform;
        this.version = version;
        this.createdAt = createdAt;
    }

    public UUID getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(UUID artifactId) {
        this.artifactId = artifactId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
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
        return Objects.equals(artifactId, artifact.artifactId) && Objects.equals(userId, artifact.userId) && Objects.equals(name, artifact.name) && Objects.equals(digest, artifact.digest) && type == artifact.type && Objects.equals(sizeBytes, artifact.sizeBytes) && platform == artifact.platform && Objects.equals(version, artifact.version) && Objects.equals(createdAt, artifact.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(artifactId, userId, name, digest, type, sizeBytes, platform, version, createdAt);
    }

    @Override
    public String toString() {
        return "Artifact{" +
                "artifactId=" + artifactId +
                ", userId=" + userId +
                ", name='" + name + '\'' +
                ", digest='" + digest + '\'' +
                ", type=" + type +
                ", sizeBytes=" + sizeBytes +
                ", platform=" + platform +
                ", version='" + version + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
