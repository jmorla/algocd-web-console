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

    @NotNull
    private UUID planId;

    @NotNull
    private UUID locationId;

    @NotBlank
    @Size(max = 20)
    private String status;

    private Instant createdAt;

    private Instant updatedAt;

    public Terminal() {
    }

    public Terminal(UUID terminalId, UUID userId, String name, MetaTraderVersion version, UUID planId, UUID locationId, String status, Instant createdAt, Instant updatedAt) {
        this.terminalId = terminalId;
        this.userId = userId;
        this.name = name;
        this.version = version;
        this.planId = planId;
        this.locationId = locationId;
        this.status = status;
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

    public UUID getPlanId() {
        return planId;
    }

    public void setPlanId(UUID planId) {
        this.planId = planId;
    }

    public UUID getLocationId() {
        return locationId;
    }

    public void setLocationId(UUID locationId) {
        this.locationId = locationId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
        return Objects.equals(terminalId, terminal.terminalId) && Objects.equals(userId, terminal.userId) && Objects.equals(name, terminal.name) && version == terminal.version && Objects.equals(planId, terminal.planId) && Objects.equals(locationId, terminal.locationId) && Objects.equals(status, terminal.status) && Objects.equals(createdAt, terminal.createdAt) && Objects.equals(updatedAt, terminal.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(terminalId, userId, name, version, planId, locationId, status, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return "Terminals{" +
                "terminalId=" + terminalId +
                ", userId=" + userId +
                ", name='" + name + '\'' +
                ", version=" + version +
                ", planId=" + planId +
                ", locationId=" + locationId +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
