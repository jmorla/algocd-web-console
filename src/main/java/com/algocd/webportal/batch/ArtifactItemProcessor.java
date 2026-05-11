package com.algocd.webportal.batch;

import com.algocd.webportal.entities.ArtifactProcessingQueue;
import com.algocd.webportal.mql.Mql5Parser;
import com.algocd.webportal.mql.tree.PropertyStatement;
import com.algocd.webportal.mql.tree.Statement;
import com.algocd.webportal.mql.tree.VariableDeclaration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.infrastructure.item.ItemProcessor;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * ItemProcessor that extracts metadata from MQL5 files.
 */
public class ArtifactItemProcessor implements ItemProcessor<ArtifactProcessingQueue, ArtifactMetadata> {

    private static final Logger logger = LoggerFactory.getLogger(ArtifactItemProcessor.class);

    @Override
    public ArtifactMetadata process(ArtifactProcessingQueue item) throws Exception {
        String filePath = item.getFilePath();
        if (filePath == null || filePath.isBlank()) {
            throw new IllegalArgumentException("Artifact file path is missing for record ID: " + item.getId());
        }

        logger.info("Processing artifact: {}", filePath);

        String content = Files.readString(Path.of(filePath));
        Mql5Parser parser = new Mql5Parser(content);
        Statement[] statements = parser.parse();

        List<PropertyStatement> properties = new ArrayList<>();
        List<VariableDeclaration> inputs = new ArrayList<>();

        for (Statement stmt : statements) {
            if (stmt instanceof PropertyStatement prop) {
                properties.add(prop);
            } else if (stmt instanceof VariableDeclaration var && 
                      (var.modifier() == VariableDeclaration.Modifier.INPUT || 
                       var.modifier() == VariableDeclaration.Modifier.EXTERN)) {
                inputs.add(var);
            }
        }

        return new ArtifactMetadata(item, properties, inputs);
    }
}
