package com.algocd.webportal.jobs;

import com.algocd.webportal.events.ArtifactUploadedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Listener that triggers artifact processing jobs when a new artifact is uploaded.
 */
@Component
public class ArtifactJobTriggerListener {

    private static final Logger logger = LoggerFactory.getLogger(ArtifactJobTriggerListener.class);

    @Async
    @EventListener
    public void onArtifactUploaded(ArtifactUploadedEvent event) {
        logger.info("ArtifactUploadedEvent received for user: {}. Total files successfully queued: {}", 
                event.userId(), event.totalFiles());
        
        // Future: Trigger Spring Batch job here
        // jobLauncher.run(artifactProcessingJob, new JobParametersBuilder()...)
    }
}
