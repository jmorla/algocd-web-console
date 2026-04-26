package com.algocd.webportal.services;

import com.algocd.webportal.services.models.CreateTerminalRecord;
import com.algocd.webportal.util.Result;

public interface TerminalService {

    Result<Void> createTerminal(CreateTerminalRecord record);
}
