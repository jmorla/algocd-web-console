package com.algocd.webportal.entities;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Terminal {

    @NotNull
    private UUID terminalId;

    @NotNull
    private TerminalStatus status;

    @Size(max = 45)
    private String instanceIp;

    private String bootstrapToken;

    private Instant bootstrapTokenExpiresAt;

    private Instant lastHeartbeatAt;

    private Instant createdAt;

    private Instant updatedAt;

    public Terminal() {
    }

    public Terminal(UUID terminalId, TerminalStatus status, String instanceIp, String bootstrapToken, Instant bootstrapTokenExpiresAt, Instant lastHeartbeatAt, Instant createdAt, Instant updatedAt) {
        this.terminalId = terminalId;
        this.status = status;
        this.instanceIp = instanceIp;
        this.bootstrapToken = bootstrapToken;
        this.bootstrapTokenExpiresAt = bootstrapTokenExpiresAt;
        this.lastHeartbeatAt = lastHeartbeatAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(UUID terminalId) {
        this.terminalId = terminalId;
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

    public String getBootstrapToken() {
        return bootstrapToken;
    }

    public void setBootstrapToken(String bootstrapToken) {
        this.bootstrapToken = bootstrapToken;
    }

    public Instant getBootstrapTokenExpiresAt() {
        return bootstrapTokenExpiresAt;
    }

    public void setBootstrapTokenExpiresAt(Instant bootstrapTokenExpiresAt) {
        this.bootstrapTokenExpiresAt = bootstrapTokenExpiresAt;
    }

    public Instant getLastHeartbeatAt() {
        return lastHeartbeatAt;
    }

    public void setLastHeartbeatAt(Instant lastHeartbeatAt) {
        this.lastHeartbeatAt = lastHeartbeatAt;
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
        Terminal terminal = (Terminal) o;
        return Objects.equals(terminalId, terminal.terminalId) && status == terminal.status && Objects.equals(instanceIp, terminal.instanceIp) && Objects.equals(bootstrapToken, terminal.bootstrapToken) && Objects.equals(bootstrapTokenExpiresAt, terminal.bootstrapTokenExpiresAt) && Objects.equals(lastHeartbeatAt, terminal.lastHeartbeatAt) && Objects.equals(createdAt, terminal.createdAt) && Objects.equals(updatedAt, terminal.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(terminalId, status, instanceIp, bootstrapToken, bootstrapTokenExpiresAt, lastHeartbeatAt, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return "Terminal{" +
                "terminalId=" + terminalId +
                ", status=" + status +
                ", instanceIp='" + instanceIp + '\'' +
                ", bootstrapToken='" + bootstrapToken + '\'' +
                ", bootstrapTokenExpiresAt=" + bootstrapTokenExpiresAt +
                ", lastHeartbeatAt=" + lastHeartbeatAt +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
