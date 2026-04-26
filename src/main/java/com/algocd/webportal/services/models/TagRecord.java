package com.algocd.webportal.services.models;

import jakarta.validation.constraints.NotBlank;

public record TagRecord(
    @NotBlank String key,
    @NotBlank String value
) {
}
