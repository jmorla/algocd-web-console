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
    private UUID userId;

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotNull
    private MetaTraderVersion version;

    @NotBlank
    @Size(max = 20)
    private String status;

    @Size(max = 45)
    private String instanceIp;

    private Instant createdAt;

    private Instant updatedAt;

    public Terminal() {
    }

    public Terminal(UUID terminalId, UUID userId, String name, MetaTraderVersion version, String status, String instanceIp, Instant createdAt, Instant updatedAt) {
        this.terminalId = terminalId;
        this.userId = userId;
        this.name = name;
        this.version = version;
        this.status = status;
        this.instanceIp = instanceIp;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(UUID terminalId) {
        this.terminalId = terminalId;
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

    public MetaTraderVersion getVersion() {
        return version;
    }

    public void setVersion(MetaTraderVersion version) {
        this.version = version;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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
        Terminal terminal = (Terminal) o;
        return Objects.equals(terminalId, terminal.terminalId) && Objects.equals(userId, terminal.userId) && Objects.equals(name, terminal.name) && version == terminal.version && Objects.equals(status, terminal.status) && Objects.equals(instanceIp, terminal.instanceIp) && Objects.equals(createdAt, terminal.createdAt) && Objects.equals(updatedAt, terminal.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(terminalId, userId, name, version, status, instanceIp, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return "Terminal{" +
                "terminalId=" + terminalId +
                ", userId=" + userId +
                ", name='" + name + '\'' +
                ", version=" + version +
                ", status='" + status + '\'' +
                ", instanceIp='" + instanceIp + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
