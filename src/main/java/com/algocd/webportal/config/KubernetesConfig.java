package com.algocd.webportal.config;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(KubernetesProperties.class)
public class KubernetesConfig {

    @Bean
    public KubernetesClient kubernetesClient(KubernetesProperties properties) {
        Config config = new ConfigBuilder()
                .withMasterUrl(properties.getMasterUrl())
                .withNamespace(properties.getNamespace())
                .withUsername(properties.getUsername())
                .withPassword(properties.getPassword())
                .withOauthToken(properties.getToken())
                .withCaCertData(properties.getCaCertData())
                .withClientCertData(properties.getClientCertData())
                .withClientKeyData(properties.getClientKeyData())
                .build();

        return new KubernetesClientBuilder().withConfig(config).build();
    }
}
