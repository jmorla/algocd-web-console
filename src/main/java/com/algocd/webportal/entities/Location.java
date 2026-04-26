package com.algocd.webportal.entities;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Objects;
import java.util.UUID;

public class Location {

    @NotNull
    private UUID locationId;

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotBlank
    @Size(max = 100)
    private String region;

    @NotNull
    private boolean enabled;

    public Location() {
    }

    public Location(UUID locationId, String name, String region, boolean enabled) {
        this.locationId = locationId;
        this.name = name;
        this.region = region;
        this.enabled = enabled;
    }

    public UUID getLocationId() {
        return locationId;
    }

    public void setLocationId(UUID locationId) {
        this.locationId = locationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return enabled == location.enabled && Objects.equals(locationId, location.locationId) && Objects.equals(name, location.name) && Objects.equals(region, location.region);
    }

    @Override
    public int hashCode() {
        return Objects.hash(locationId, name, region, enabled);
    }

    @Override
    public String toString() {
        return "Locations{" +
                "locationId=" + locationId +
                ", name='" + name + '\'' +
                ", region='" + region + '\'' +
                ", enabled=" + enabled +
                '}';
    }
}
