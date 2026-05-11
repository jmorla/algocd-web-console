package com.algocd.webportal.batch;

import com.algocd.webportal.entities.ArtifactProcessingQueue;
import com.algocd.webportal.mql.tree.PropertyStatement;
import com.algocd.webportal.mql.tree.VariableDeclaration;

import java.util.List;

/**
 * DTO that encapsulates the original processing record and the metadata extracted from the file.
 */
public record ArtifactMetadata(
    ArtifactProcessingQueue queueRecord,
    List<PropertyStatement> properties,
    List<VariableDeclaration> inputs
) {}
