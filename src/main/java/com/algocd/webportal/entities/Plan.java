package com.algocd.webportal.entities;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class Plan {

    @NotNull
    private UUID planId;

    @NotBlank
    @Size(max = 50)
    private String name;

    @NotNull
    private int cpuCores;

    @NotNull
    private int ramGb;

    @NotNull
    private BigDecimal monthlyPrice;

    @NotNull
    private BigDecimal hourlyPrice;

    @NotNull
    private int expertLimit;

    public Plan() {
    }

    public Plan(UUID planId, String name, int cpuCores, int ramGb, BigDecimal monthlyPrice, BigDecimal hourlyPrice, int expertLimit) {
        this.planId = planId;
        this.name = name;
        this.cpuCores = cpuCores;
        this.ramGb = ramGb;
        this.monthlyPrice = monthlyPrice;
        this.hourlyPrice = hourlyPrice;
        this.expertLimit = expertLimit;
    }

    public UUID getPlanId() {
        return planId;
    }

    public void setPlanId(UUID planId) {
        this.planId = planId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Plan plan = (Plan) o;
        return cpuCores == plan.cpuCores && ramGb == plan.ramGb && expertLimit == plan.expertLimit && Objects.equals(planId, plan.planId) && Objects.equals(name, plan.name) && Objects.equals(monthlyPrice, plan.monthlyPrice) && Objects.equals(hourlyPrice, plan.hourlyPrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(planId, name, cpuCores, ramGb, monthlyPrice, hourlyPrice, expertLimit);
    }

    @Override
    public String toString() {
        return "Plans{" +
                "planId=" + planId +
                ", name='" + name + '\'' +
                ", cpuCores=" + cpuCores +
                ", ramGb=" + ramGb +
                ", monthlyPrice=" + monthlyPrice +
                ", hourlyPrice=" + hourlyPrice +
                ", expertLimit=" + expertLimit +
                '}';
    }
}
