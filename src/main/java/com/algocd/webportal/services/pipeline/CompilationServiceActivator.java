package com.algocd.webportal.services.pipeline;

import com.algocd.webportal.config.ArtifactPipelineProperties;
import com.algocd.webportal.entities.ArtifactProcessingQueue;
import com.algocd.webportal.entities.ProcessingStatus;
import com.algocd.webportal.exceptions.ArtifactPipelineException;
import com.algocd.webportal.exceptions.ErrorReason;
import com.algocd.webportal.mappers.ArtifactProcessingQueueMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@Service
public class CompilationServiceActivator {

    private static final Logger logger = LoggerFactory.getLogger(CompilationServiceActivator.class);

    private final ArtifactProcessingQueueMapper queueMapper;
    private final ArtifactPipelineProperties properties;
    private final Map<UUID, Semaphore> userSemaphores = new ConcurrentHashMap<>();

    public CompilationServiceActivator(ArtifactProcessingQueueMapper queueMapper, ArtifactPipelineProperties properties) {
        this.queueMapper = queueMapper;
        this.properties = properties;
    }

    @ServiceActivator(inputChannel = "compilationInputChannel", outputChannel = "uploadInputChannel", adviceChain = "compilationAdvice")
    public ArtifactProcessingQueue compile(ArtifactProcessingQueue record) throws Exception {
        UUID userId = record.getUserId();
        Semaphore semaphore = userSemaphores.computeIfAbsent(userId, k -> new Semaphore(properties.getMaxConcurrentPerUser()));

        semaphore.acquire();
        try {
            logger.info("Starting compilation for file: {} (User: {})", record.getOriginalFilename(), userId);
            queueMapper.updateStatus(record.getId(), ProcessingStatus.COMPILING, "artifact.status.compiling", Instant.now());

            performCompilation(record);

            logger.info("Compilation completed for file: {} (User: {})", record.getOriginalFilename(), userId);
            queueMapper.updateStatus(record.getId(), ProcessingStatus.COMPILED, "artifact.status.compiled", Instant.now());

            return record;
        } finally {
            semaphore.release();
        }
    }

    private void performCompilation(ArtifactProcessingQueue record) {
        String compilerPath = properties.getCompilerPath();
        String filePath = record.getFilePath();

        try {
            ProcessBuilder pb = new ProcessBuilder(compilerPath, filePath);
            pb.directory(Paths.get(".").toFile()); // Ensure it runs from project root
            pb.redirectErrorStream(true);

            Process process = pb.start();
            StringBuilder output = new StringBuilder();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            boolean completed = process.waitFor(60, TimeUnit.SECONDS);
            if (!completed) {
                process.destroyForcibly();
                throw new ArtifactPipelineException(ErrorReason.ARTIFACT_COMPILATION_FAILED,
                        "Compilation timed out after 60 seconds", record.getUserId(), record.getOriginalFilename());
            }

            int exitCode = process.exitValue();
            if (exitCode != 0) {
                logger.error("Compilation failed with exit code {}. Output:\n{}", exitCode, output);
                throw new ArtifactPipelineException(ErrorReason.ARTIFACT_COMPILATION_FAILED,
                        "Compilation failed with exit code " + exitCode, record.getUserId(), record.getOriginalFilename());
            }

        } catch (ArtifactPipelineException e) {
            throw e;
        } catch (Exception e) {
            throw new ArtifactPipelineException(ErrorReason.ARTIFACT_COMPILATION_FAILED,
                    "Internal error during compilation: " + e.getMessage(), e, record.getUserId(), record.getOriginalFilename());
        }
    }
}
