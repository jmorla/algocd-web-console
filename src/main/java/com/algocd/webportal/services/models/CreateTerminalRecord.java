package com.algocd.webportal.services.models;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record CreateTerminalRecord(
    @NotBlank String version,
    @NotBlank String plan,
    @NotBlank String location,
    List<String> tags,
    @NotBlank String name,
    @NotBlank String accountId,
    @NotBlank String brokerServer,
    @NotBlank String password
) {
}
