package com.algocd.webportal.services.pipeline;

import com.algocd.webportal.entities.ArtifactProcessingQueue;
import com.algocd.webportal.entities.ProcessingStatus;
import com.algocd.webportal.exceptions.ArtifactPipelineException;
import com.algocd.webportal.mappers.ArtifactProcessingQueueMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class PipelineErrorHandler {

    private static final Logger logger = LoggerFactory.getLogger(PipelineErrorHandler.class);
    private final ArtifactProcessingQueueMapper queueMapper;

    public PipelineErrorHandler(ArtifactProcessingQueueMapper queueMapper) {
        this.queueMapper = queueMapper;
    }

    @ServiceActivator(inputChannel = "analysisErrorChannel")
    public void handleAnalysisError(Message<?> message) {
        handleError(message, "Analysis");
    }

    @ServiceActivator(inputChannel = "compilationErrorChannel")
    public void handleCompilationError(Message<?> message) {
        handleError(message, "Compilation");
    }

    @ServiceActivator(inputChannel = "uploadErrorChannel")
    public void handleUploadError(Message<?> message) {
        handleError(message, "Upload");
    }

    private void handleError(Message<?> message, String phase) {
        Object payload = message.getPayload();
        // ExpressionEvaluatingRequestHandlerAdvice puts the exception in "x-exception" header
        Throwable cause = message.getHeaders().get("x-exception", Throwable.class);
        ArtifactProcessingQueue record = null;

        if (payload instanceof ArtifactProcessingQueue) {
            record = (ArtifactProcessingQueue) payload;
        } else if (payload instanceof MessagingException mex) {
            cause = mex.getCause();
            if (mex.getFailedMessage() != null && mex.getFailedMessage().getPayload() instanceof ArtifactProcessingQueue) {
                record = (ArtifactProcessingQueue) mex.getFailedMessage().getPayload();
            }
        }

        if (record == null) {
            logger.error("Received error message in {} phase but could not extract artifact record: {}", phase, message);
            return;
        }

        // Unwrap recursive MessagingExceptions or generic wrappers to find the actual cause
        Throwable rootCause = findRootCause(cause);

        if (rootCause instanceof ArtifactPipelineException ape) {
            logger.error("{} phase failed for artifact: {} (User: {}). Reason: {} - {}", 
                phase, ape.getFilename(), ape.getUserId(), ape.getErrorCode(), ape.getMessage());
            queueMapper.updateStatus(record.getId(), ProcessingStatus.FAILED, ape.getMessage(), Instant.now());
        } else {
            logger.error("{} phase failed for artifact: {}. Unexpected error occurred:", 
                phase, record.getOriginalFilename(), rootCause);
            queueMapper.updateStatus(record.getId(), ProcessingStatus.FAILED, "artifact.status.failed", Instant.now());
        }
    }

    private Throwable findRootCause(Throwable throwable) {
        if (throwable == null) return null;
        Throwable root = throwable;
        while (root.getCause() != null && root.getCause() != root && 
              (root instanceof MessagingException || root.getClass().getName().contains("MessageProcessor"))) {
            root = root.getCause();
        }
        return root;
    }
}
