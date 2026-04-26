package com.algocd.webportal.services.models;

import com.algocd.webportal.entities.MetaTraderVersion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public record CreateTerminalRecord(
    @NotNull MetaTraderVersion version,
    @NotNull UUID planId,
    @NotNull UUID locationId,
    List<TagRecord> tags,
    @NotBlank String name,
    @NotBlank String accountId,
    @NotBlank String brokerServer,
    @NotBlank String password
) {
}
