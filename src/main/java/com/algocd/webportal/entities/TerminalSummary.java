package com.algocd.webportal.entities;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class TerminalSummary {
    private UUID terminalId;
    private String name;
    private MetaTraderVersion version;
    private String status;
    private String instanceIp;
    private Instant createdAt;
    private Instant updatedAt;

    // Plan info
    private String planName;
    private int cpuCores;
    private int ramGb;
    private BigDecimal monthlyPrice;
    private BigDecimal hourlyPrice;
    private int expertLimit;

    // Location info
    private String locationName;
    private String locationRegion;

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

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public int getCpuCores() {
        return cpuCores;
    }

    public void setCpuCores(int cpuCores) {
        this.cpuCores = cpuCores;
    }

    public int getRamGb() {
        return ramGb;
    }

    public void setRamGb(int ramGb) {
        this.ramGb = ramGb;
    }

    public BigDecimal getMonthlyPrice() {
        return monthlyPrice;
    }

    public void setMonthlyPrice(BigDecimal monthlyPrice) {
        this.monthlyPrice = monthlyPrice;
    }

    public BigDecimal getHourlyPrice() {
        return hourlyPrice;
    }

    public void setHourlyPrice(BigDecimal hourlyPrice) {
        this.hourlyPrice = hourlyPrice;
    }

    public int getExpertLimit() {
        return expertLimit;
    }

    public void setExpertLimit(int expertLimit) {
        this.expertLimit = expertLimit;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getLocationRegion() {
        return locationRegion;
    }

    public void setLocationRegion(String locationRegion) {
        this.locationRegion = locationRegion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TerminalSummary that = (TerminalSummary) o;
        return cpuCores == that.cpuCores && ramGb == that.ramGb && expertLimit == that.expertLimit && Objects.equals(terminalId, that.terminalId) && Objects.equals(name, that.name) && version == that.version && Objects.equals(status, that.status) && Objects.equals(instanceIp, that.instanceIp) && Objects.equals(createdAt, that.createdAt) && Objects.equals(updatedAt, that.updatedAt) && Objects.equals(planName, that.planName) && Objects.equals(monthlyPrice, that.monthlyPrice) && Objects.equals(hourlyPrice, that.hourlyPrice) && Objects.equals(locationName, that.locationName) && Objects.equals(locationRegion, that.locationRegion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(terminalId, name, version, status, instanceIp, createdAt, updatedAt, planName, cpuCores, ramGb, monthlyPrice, hourlyPrice, expertLimit, locationName, locationRegion);
    }

    @Override
    public String toString() {
        return "TerminalSummary{" +
                "terminalId=" + terminalId +
                ", name='" + name + '\'' +
                ", version=" + version +
                ", status='" + status + '\'' +
                ", instanceIp='" + instanceIp + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", planName='" + planName + '\'' +
                ", cpuCores=" + cpuCores +
                ", ramGb=" + ramGb +
                ", monthlyPrice=" + monthlyPrice +
                ", hourlyPrice=" + hourlyPrice +
                ", expertLimit=" + expertLimit +
                ", locationName='" + locationName + '\'' +
                ", locationRegion='" + locationRegion + '\'' +
                '}';
    }
}
