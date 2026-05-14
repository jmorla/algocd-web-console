package com.algocd.webportal.services;

import com.algocd.webportal.util.Result;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface ArtifactService {
    Result<UUID> processAndQueueArtifacts(MultipartFile[] files, UUID userId);
    java.util.List<com.algocd.webportal.entities.ArtifactProcessingQueue> getQueueStatus(UUID batchId);
}
