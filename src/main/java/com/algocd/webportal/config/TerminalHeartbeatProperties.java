package com.algocd.webportal.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "algocd.terminal.heartbeat")
public record TerminalHeartbeatProperties(
        Duration interval,
        Duration ttl
) {
    public TerminalHeartbeatProperties {
        if (interval == null) {
            interval = Duration.ofSeconds(60);
        }
        if (ttl == null) {
            ttl = Duration.ofSeconds(300);
        }
    }
}
