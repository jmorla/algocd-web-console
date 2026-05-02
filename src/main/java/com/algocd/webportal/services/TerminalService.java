package com.algocd.webportal.services;

import com.algocd.webportal.entities.Terminal;
import com.algocd.webportal.services.models.CreateTerminalRequest;
import com.algocd.webportal.util.Result;

import java.util.UUID;

public interface TerminalService {
    Result<Terminal> createTerminal(UUID userId, CreateTerminalRequest request);
    Result<Terminal> bootstrapTerminal(String bootstrapToken, String instanceIp);
}
