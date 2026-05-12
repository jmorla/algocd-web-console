package com.algocd.webportal.services;

import com.algocd.webportal.entities.ArtifactProcessingQueue;
import com.algocd.webportal.entities.ProcessingStatus;
import com.algocd.webportal.mappers.ArtifactProcessingQueueMapper;
import com.algocd.webportal.services.pipeline.ArtifactPipelineGateway;
import com.algocd.webportal.util.DigestUtil;
import com.algocd.webportal.util.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.time.Instant;
import java.util.UUID;

@Service
public class ArtifactServiceImpl implements ArtifactService {

    private static final Logger logger = LoggerFactory.getLogger(ArtifactServiceImpl.class);

    private final StorageService storageService;
    private final ArtifactProcessingQueueMapper queueMapper;
    private final ArtifactPipelineGateway gateway;

    public ArtifactServiceImpl(StorageService storageService, ArtifactProcessingQueueMapper queueMapper, ArtifactPipelineGateway gateway) {
        this.storageService = storageService;
        this.queueMapper = queueMapper;
        this.gateway = gateway;
    }

    @Override
    public Result<Void> processAndQueueArtifacts(MultipartFile[] files, UUID userId) {
        if (files == null) {
            return Result.success(null);
        }

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue;
            }

            ArtifactProcessingQueue record = new ArtifactProcessingQueue();
            record.setUserId(userId);
            record.setOriginalFilename(file.getOriginalFilename());
            record.setCreatedAt(Instant.now());
            record.setUpdatedAt(Instant.now());

            Result<Path> storeResult = storageService.store(file);
            if (storeResult.isFailure()) {
                logger.error("Failed to store file {}", file.getOriginalFilename(), storeResult.getError());
                record.setStatus(ProcessingStatus.FAILED);
                record.setMessage("artifact.queue.error.storage");
                record.setFilePath(""); // No path if storage failed
                queueMapper.insert(record);
                continue;
            }

            Path storedPath = storeResult.getValue();
            record.setFilePath(storedPath.toString());

            Result<String> digestResult = DigestUtil.calculateSha256(storedPath);
            if (digestResult.isFailure()) {
                logger.error("Failed to calculate digest for file {}", storedPath, digestResult.getError());
                record.setStatus(ProcessingStatus.FAILED);
                record.setMessage("artifact.queue.error.digest");
                queueMapper.insert(record);
                continue;
            }

            record.setDigest(digestResult.getValue());
            record.setStatus(ProcessingStatus.QUEUED);

            try {
                queueMapper.insert(record);
                gateway.queueForAnalysis(record);
            } catch (Exception e) {
                logger.error("Failed to insert queue record for file {}", file.getOriginalFilename(), e);
            }
        }

        return Result.success(null);
    }
}
