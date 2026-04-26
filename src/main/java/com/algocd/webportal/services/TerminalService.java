package com.algocd.webportal.services;

import com.algocd.webportal.services.models.CreateTerminalRecord;
import com.algocd.webportal.util.Result;

import java.util.UUID;

public interface TerminalService {

    Result<Void> createTerminal(UUID userId, CreateTerminalRecord record);
}
