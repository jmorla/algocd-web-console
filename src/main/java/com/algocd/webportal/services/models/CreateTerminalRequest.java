package com.algocd.webportal.services.models;

import com.algocd.webportal.entities.Platform;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Map;

public record CreateTerminalRequest(
    @NotBlank(message = "{terminal.create.error.name.required}") 
    @Size(max = 100, message = "{terminal.create.error.name.size}")
    String name,
    
    @NotNull(message = "{terminal.create.error.platform.required}") 
    Platform platform,
    
    Map<String, String> tags
) {}
