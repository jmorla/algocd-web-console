package com.algocd.webportal.services.pipeline;

import com.algocd.webportal.config.ArtifactPipelineProperties;
import com.algocd.webportal.entities.ArtifactProcessingQueue;
import com.algocd.webportal.entities.ProcessingStatus;
import com.algocd.webportal.exceptions.ArtifactPipelineException;
import com.algocd.webportal.exceptions.ErrorReason;
import com.algocd.webportal.mappers.ArtifactProcessingQueueMapper;
import com.algocd.webportal.mql.Mql5Parser;
import com.algocd.webportal.mql.tree.Statement;
import com.algocd.webportal.services.pipeline.models.ArtifactConfig;
import com.algocd.webportal.services.pipeline.util.ArtifactAnalysisUtils;
import com.algocd.webportal.services.pipeline.util.ArtifactConstants;
import com.algocd.webportal.services.pipeline.util.ArtifactPipelineUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

@Service
public class AnalysisServiceActivator {

    private static final Logger logger = LoggerFactory.getLogger(AnalysisServiceActivator.class);

    private final ArtifactProcessingQueueMapper queueMapper;
    private final ArtifactPipelineProperties properties;
    private final ObjectMapper objectMapper;
    private final Map<UUID, Semaphore> userSemaphores = new ConcurrentHashMap<>();

    public AnalysisServiceActivator(ArtifactProcessingQueueMapper queueMapper, ArtifactPipelineProperties properties, ObjectMapper objectMapper) {
        this.queueMapper = queueMapper;
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    @ServiceActivator(inputChannel = "analysisInputChannel", outputChannel = "compilationInputChannel", adviceChain = "analysisAdvice")
    public ArtifactProcessingQueue analyze(ArtifactProcessingQueue record) throws Exception {
        UUID userId = record.getUserId();
        Semaphore semaphore = userSemaphores.computeIfAbsent(userId, k -> new Semaphore(properties.getMaxConcurrentPerUser()));

        semaphore.acquire();
        try {
            logger.info("Starting analysis for file: {} (User: {})", record.getOriginalFilename(), userId);
            
            queueMapper.updateStatus(record.getId(), ProcessingStatus.ANALYZING, "artifact.status.analyzing", Instant.now());

            performAnalysis(record);

            logger.info("Analysis completed for file: {} (User: {})", record.getOriginalFilename(), userId);
            queueMapper.updateStatus(record.getId(), ProcessingStatus.ANALYZED, "artifact.status.analyzed", Instant.now());

            return record;
        } finally {
            semaphore.release();
        }
    }

    private void performAnalysis(ArtifactProcessingQueue record) {
        String filePath = record.getFilePath();
        Statement[] statements;
        try {
            String content = Files.readString(Paths.get(filePath));
            Mql5Parser parser = new Mql5Parser(content);
            statements = parser.parse();
        } catch (Exception e) {
            throw new ArtifactPipelineException(ErrorReason.ARTIFACT_ANALYSIS_FAILED, 
                "Failed to parse MQL5 file: " + e.getMessage(), e, record.getUserId(), record.getOriginalFilename());
        }

        ArtifactConfig config = ArtifactAnalysisUtils.extractConfig(statements, record.getOriginalFilename(), record.getUserId());

        String configPath = ArtifactPipelineUtils.getBaseFilePath(filePath) + ArtifactConstants.CONFIG_EXTENSION;
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(Paths.get(configPath).toFile(), config);
        } catch (Exception e) {
            throw new ArtifactPipelineException(ErrorReason.ARTIFACT_ANALYSIS_FAILED, 
                "Failed to write artifact configuration JSON", e, record.getUserId(), record.getOriginalFilename());
        }
    }
}
