package com.algocd.webportal.config;

import land.oras.Registry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for the ORAS client.
 */
@Configuration
public class OrasConfig {

    /**
     * Creates a {@link Registry} bean for interacting with OCI registries.
     *
     * @param properties The OCI registry properties.
     * @return A configured {@link Registry} instance.
     */
    @Bean
    public Registry orasRegistry(OciRegistryProperties properties) {
        Registry.Builder builder = Registry.builder();
        
        if (properties.username() != null && !properties.username().isBlank() &&
                properties.password() != null && !properties.password().isBlank()) {
            builder.defaults(properties.username(), properties.password());
        } else {
            builder.defaults();
        }

        if (!properties.tlsEnabled()) {
            builder.insecure();
        }

        return builder.build();
    }
}
