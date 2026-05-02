package com.algocd.webportal.jobs;

import com.algocd.webportal.config.TerminalHeartbeatProperties;
import com.algocd.webportal.services.TerminalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TerminalHeartbeatJob {

    private static final Logger log = LoggerFactory.getLogger(TerminalHeartbeatJob.class);

    private final TerminalService terminalService;
    private final TerminalHeartbeatProperties properties;

    public TerminalHeartbeatJob(TerminalService terminalService, TerminalHeartbeatProperties properties) {
        this.terminalService = terminalService;
        this.properties = properties;
    }

    @Scheduled(fixedRateString = "${algocd.terminal.heartbeat.interval:60s}")
    public void checkHeartbeats() {
        log.debug("Checking for stale terminal heartbeats...");
        int updated = terminalService.markStaleTerminalsAsDisconnected(properties.ttl());
        if (updated > 0) {
            log.info("Marked {} stale terminals as DISCONNECTED", updated);
        }
    }
}
