package com.algocd.webportal.events;

import java.util.UUID;

/**
 * Event published when an artifact has been successfully uploaded and queued.
 * This event is intended to trigger asynchronous processing, such as Spring Batch jobs.
 */
public record ArtifactUploadedEvent(UUID userId, int totalFiles) {
}
