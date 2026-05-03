package com.algocd.webportal.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the application's security credentials.
 */
@ConfigurationProperties(prefix = "algocd.app.credentials")
public record AppCredentialsProperties(
        String privateKeyPath,
        String certificatePath,
        String caCertificatePath
) {
}
