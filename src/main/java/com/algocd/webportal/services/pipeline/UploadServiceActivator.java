package com.algocd.webportal.services.pipeline;

import com.algocd.webportal.config.ArtifactPipelineProperties;
import com.algocd.webportal.config.OciRegistryProperties;
import com.algocd.webportal.entities.ArtifactProcessingQueue;
import com.algocd.webportal.entities.ProcessingStatus;
import com.algocd.webportal.entities.User;
import com.algocd.webportal.exceptions.ArtifactPipelineException;
import com.algocd.webportal.exceptions.ErrorReason;
import com.algocd.webportal.mappers.ArtifactProcessingQueueMapper;
import com.algocd.webportal.mappers.UserMapper;
import com.algocd.webportal.services.pipeline.models.ArtifactConfig;
import com.algocd.webportal.services.pipeline.util.ArtifactConstants;
import com.algocd.webportal.services.pipeline.util.ArtifactPipelineUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import land.oras.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

@Service
public class UploadServiceActivator {

    private static final Logger logger = LoggerFactory.getLogger(UploadServiceActivator.class);

    private final ArtifactProcessingQueueMapper queueMapper;
    private final UserMapper userMapper;
    private final Registry orasRegistry;
    private final OciRegistryProperties registryProperties;
    private final ArtifactPipelineProperties pipelineProperties;
    private final ObjectMapper objectMapper;
    private final Map<UUID, Semaphore> userSemaphores = new ConcurrentHashMap<>();

    public UploadServiceActivator(ArtifactProcessingQueueMapper queueMapper,
                                  UserMapper userMapper,
                                  Registry orasRegistry,
                                  OciRegistryProperties registryProperties,
                                  ArtifactPipelineProperties pipelineProperties,
                                  ObjectMapper objectMapper) {
        this.queueMapper = queueMapper;
        this.userMapper = userMapper;
        this.orasRegistry = orasRegistry;
        this.registryProperties = registryProperties;
        this.pipelineProperties = pipelineProperties;
        this.objectMapper = objectMapper;
    }

    @ServiceActivator(inputChannel = "uploadInputChannel", adviceChain = "uploadAdvice")
    public void upload(ArtifactProcessingQueue record) throws Exception {
        UUID userId = record.getUserId();
        Semaphore semaphore = userSemaphores.computeIfAbsent(userId, k -> new Semaphore(pipelineProperties.getMaxConcurrentPerUser()));

        semaphore.acquire();
        try {
            logger.info("Starting upload for file: {} (User: {})", record.getOriginalFilename(), userId);
            queueMapper.updateStatus(record.getId(), ProcessingStatus.UPLOADING, "artifact.status.uploading", Instant.now());

            performUpload(record);

            logger.info("Upload completed for file: {} (User: {})", record.getOriginalFilename(), userId);
            queueMapper.updateStatus(record.getId(), ProcessingStatus.COMPLETED, "artifact.status.completed", Instant.now());

        } finally {
            semaphore.release();
        }
    }

    private void performUpload(ArtifactProcessingQueue record) {
        try {
            User user = userMapper.findById(record.getUserId())
                    .orElseThrow(() -> new ArtifactPipelineException(ErrorReason.INTERNAL_SERVER_ERROR,
                            "User not found", record.getUserId(), record.getOriginalFilename()));

            String basePath = ArtifactPipelineUtils.getBaseFilePath(record.getFilePath());
            Path configPath = Paths.get(basePath + ArtifactConstants.CONFIG_EXTENSION);
            Path ex5Path = Paths.get(basePath + ArtifactConstants.EX5_EXTENSION);

            ArtifactConfig artifactConfig = objectMapper.readValue(configPath.toFile(), ArtifactConfig.class);

            String kind = artifactConfig.getKind(); // expert or indicator
            String name = artifactConfig.getMetadata().getName();
            String artifactId = artifactConfig.getMetadata().getId();
            String version = artifactConfig.getVersion();
            String username = user.getUsername();

            // Construct ContainerRef using artifactId for OCI compliance
            // Path: <host>:<port>/algocd/<username>/<kind>/<artifactId>:<version>
            String repoPath = String.format("algocd/%s/%ss/%s", username, kind, artifactId);
            ContainerRef ref = ContainerRef.parse(registryProperties.getUrl() + "/" + repoPath + ":" + version);

            // Push blobs
            Layer configBlob = orasRegistry.pushBlob(ref, configPath, Map.of());
            Layer ex5Blob = orasRegistry.pushBlob(ref, ex5Path, Map.of());

            // Build Config and Layer with custom types and annotations
            Config config = Config.fromBlob(ArtifactConstants.MQL5_CONFIG_MEDIA_TYPE, configBlob);

            String artifactType = ArtifactConstants.MQL5_ARTIFACT_TYPE_BASE + kind + "+ex5";
            String title = ArtifactPipelineUtils.buildArtifactTitle(username, kind, ArtifactPipelineUtils.slugify(name), version);

            Layer decoratedEx5Layer = ex5Blob.withMediaType(ArtifactConstants.MQL5_EX5_LAYER_MEDIA_TYPE)
                    .withAnnotations(Map.of(ArtifactConstants.OCI_IMAGE_TITLE, title));

            Manifest manifest = Manifest.empty()
                    .withArtifactType(ArtifactType.from(artifactType))
                    .withConfig(config)
                    .withLayers(List.of(decoratedEx5Layer))
                    .withAnnotations(Map.of(
                            ArtifactConstants.OCI_IMAGE_TITLE, title,
                            ArtifactConstants.OCI_IMAGE_CREATED, Instant.now().toString()
                    ));

            orasRegistry.pushManifest(ref, manifest);

        } catch (ArtifactPipelineException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Failed to upload artifact: {}", record.getOriginalFilename(), e);
            throw new ArtifactPipelineException(ErrorReason.ARTIFACT_UPLOAD_FAILED,
                    "Internal error during upload: " + e.getMessage(), e, record.getUserId(), record.getOriginalFilename());
        }
    }
}
