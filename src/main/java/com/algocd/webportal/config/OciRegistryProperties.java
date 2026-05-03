package com.algocd.webportal.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the OCI Registry.
 */
@ConfigurationProperties(prefix = "algocd.registry")
public record OciRegistryProperties(
        String host,
        int port,
        String username,
        String password,
        boolean tlsEnabled
) {
    public OciRegistryProperties {
        if (host == null) {
            host = "localhost";
        }
        if (port == 0) {
            port = 5000;
        }
    }

    /**
     * Gets the full registry URL.
     * @return The registry URL in host:port format.
     */
    public String getUrl() {
        return host + ":" + port;
    }
}
