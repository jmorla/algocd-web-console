package com.algocd.webportal.entities;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class TerminalSummary {
    private UUID terminalId;
    private String name;
    private Platform platform;
    private TerminalStatus status;
    private String instanceIp;
    private Instant createdAt;
    private Instant updatedAt;

    public TerminalSummary() {
    }

    public UUID getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(UUID terminalId) {
        this.terminalId = terminalId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    public TerminalStatus getStatus() {
        return status;
    }

    public void setStatus(TerminalStatus status) {
        this.status = status;
    }

    public String getInstanceIp() {
        return instanceIp;
    }

    public void setInstanceIp(String instanceIp) {
        this.instanceIp = instanceIp;
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
        TerminalSummary that = (TerminalSummary) o;
        return Objects.equals(terminalId, that.terminalId) && Objects.equals(name, that.name) && platform == that.platform && status == that.status && Objects.equals(instanceIp, that.instanceIp) && Objects.equals(createdAt, that.createdAt) && Objects.equals(updatedAt, that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(terminalId, name, platform, status, instanceIp, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return "TerminalSummary{" +
                "terminalId=" + terminalId +
                ", name='" + name + '\'' +
                ", platform=" + platform +
                ", status=" + status +
                ", instanceIp='" + instanceIp + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
