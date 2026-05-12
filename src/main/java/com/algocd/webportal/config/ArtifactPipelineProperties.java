package com.algocd.webportal.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "algocd.artifact.pipeline")
public class ArtifactPipelineProperties {

    /**
     * Maximum number of concurrent files being processed per user in a single phase.
     */
    private int maxConcurrentPerUser = 5;

    /**
     * Path to the bash script that invokes MetaEditor.
     */
    private String compilerPath = "./compile_mq5.sh";

    public int getMaxConcurrentPerUser() {
        return maxConcurrentPerUser;
    }

    public void setMaxConcurrentPerUser(int maxConcurrentPerUser) {
        this.maxConcurrentPerUser = maxConcurrentPerUser;
    }

    public String getCompilerPath() {
        return compilerPath;
    }

    public void setCompilerPath(String compilerPath) {
        this.compilerPath = compilerPath;
    }
}
