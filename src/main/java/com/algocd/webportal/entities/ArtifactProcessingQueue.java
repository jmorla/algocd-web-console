package com.algocd.webportal.entities;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class ArtifactProcessingQueue {

    private Long id;

    @NotNull
    private UUID userId;

    @NotNull
    private UUID batchId;

    @NotBlank
    @Size(max = 1024)
    private String filePath;

    @NotBlank
    @Size(max = 255)
    private String originalFilename;

    @NotNull
    private ProcessingStatus status;

    private String message;

    @Size(max = 255)
    private String digest;

    private Long sizeBytes;

    private Instant createdAt;

    private Instant updatedAt;

    public ArtifactProcessingQueue() {
    }

    public ArtifactProcessingQueue(Long id, UUID userId, UUID batchId, String filePath, String originalFilename, ProcessingStatus status, String message, String digest, Long sizeBytes, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.userId = userId;
        this.batchId = batchId;
        this.filePath = filePath;
        this.originalFilename = originalFilename;
        this.status = status;
        this.message = message;
        this.digest = digest;
        this.sizeBytes = sizeBytes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getBatchId() {
        return batchId;
    }

    public void setBatchId(UUID batchId) {
        this.batchId = batchId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public ProcessingStatus getStatus() {
        return status;
    }

    public void setStatus(ProcessingStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public Long getSizeBytes() {
        return sizeBytes;
    }

    public void setSizeBytes(Long sizeBytes) {
        this.sizeBytes = sizeBytes;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArtifactProcessingQueue that = (ArtifactProcessingQueue) o;
        return Objects.equals(id, that.id) && Objects.equals(userId, that.userId) && Objects.equals(batchId, that.batchId) && Objects.equals(filePath, that.filePath) && Objects.equals(originalFilename, that.originalFilename) && Objects.equals(status, that.status) && Objects.equals(message, that.message) && Objects.equals(digest, that.digest) && Objects.equals(sizeBytes, that.sizeBytes) && Objects.equals(createdAt, that.createdAt) && Objects.equals(updatedAt, that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, batchId, filePath, originalFilename, status, message, digest, sizeBytes, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return "ArtifactProcessingQueue{" +
                "id=" + id +
                ", userId=" + userId +
                ", batchId=" + batchId +
                ", filePath='" + filePath + '\'' +
                ", originalFilename='" + originalFilename + '\'' +
                ", status=" + status +
                ", message='" + message + '\'' +
                ", digest='" + digest + '\'' +
                ", sizeBytes=" + sizeBytes +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
