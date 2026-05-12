package com.algocd.webportal.services.pipeline;

import com.algocd.webportal.entities.ArtifactProcessingQueue;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway
public interface ArtifactPipelineGateway {

    @Gateway(requestChannel = "analysisInputChannel")
    void queueForAnalysis(ArtifactProcessingQueue record);
}
