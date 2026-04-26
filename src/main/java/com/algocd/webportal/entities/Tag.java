package com.algocd.webportal.entities;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Objects;

public class Tag {

    @NotBlank
    @Size(max = 255)
    private String resourceId;

    @NotBlank
    @Size(max = 128)
    private String tagKey;

    @Size(max = 256)
    private String tagValue;

    public Tag() {
    }

    public Tag(String resourceId, String tagKey, String tagValue) {
        this.resourceId = resourceId;
        this.tagKey = tagKey;
        this.tagValue = tagValue;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getTagKey() {
        return tagKey;
    }

    public void setTagKey(String tagKey) {
        this.tagKey = tagKey;
    }

    public String getTagValue() {
        return tagValue;
    }

    public void setTagValue(String tagValue) {
        this.tagValue = tagValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return Objects.equals(resourceId, tag.resourceId) && Objects.equals(tagKey, tag.tagKey) && Objects.equals(tagValue, tag.tagValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(resourceId, tagKey, tagValue);
    }

    @Override
    public String toString() {
        return "Tags{" +
                "resourceId='" + resourceId + '\'' +
                ", tagKey='" + tagKey + '\'' +
                ", tagValue='" + tagValue + '\'' +
                '}';
    }
}
