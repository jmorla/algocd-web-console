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
        if (properties.username() != null && !properties.username().isBlank() &&
                properties.password() != null && !properties.password().isBlank()) {
            return Registry.builder()
                    .defaults(properties.username(), properties.password())
                    .build();
        }
        return Registry.builder()
                .defaults()
                .build();
    }
}
