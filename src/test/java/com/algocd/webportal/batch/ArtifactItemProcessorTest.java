package com.algocd.webportal.batch;

import com.algocd.webportal.entities.ArtifactProcessingQueue;
import com.algocd.webportal.entities.ProcessingStatus;
import com.algocd.webportal.mql.tree.VariableDeclaration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ArtifactItemProcessorTest {

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("Given a valid MQL5 file, the processor should extract properties and inputs")
    void givenValidMql5_whenProcess_thenMetadataExtracted() throws Exception {
        // Prepare MQL5 content
        String content = """
            #property description "Test Artifact"
            #property version     "1.00"
            
            input int      MagicNumber = 123456;
            input double   StopLoss    = 50.0;
            extern string  SymbolName  = "EURUSD";
            
            void OnTick() {
               // Logic here
            }
            """;
        
        Path filePath = tempDir.resolve("test.mq5");
        Files.writeString(filePath, content);

        // Prepare queue item
        ArtifactProcessingQueue item = new ArtifactProcessingQueue();
        item.setFilePath(filePath.toString());
        item.setOriginalFilename("test.mq5");
        item.setUserId(UUID.randomUUID());
        item.setStatus(ProcessingStatus.QUEUED);
        item.setCreatedAt(Instant.now());
        item.setUpdatedAt(Instant.now());

        ArtifactItemProcessor processor = new ArtifactItemProcessor();
        ArtifactMetadata metadata = processor.process(item);

        assertThat(metadata).isNotNull();
        assertThat(metadata.queueRecord()).isEqualTo(item);
        
        // Check properties
        assertThat(metadata.properties()).hasSize(2);
        assertThat(metadata.properties().get(0).name()).isEqualTo("description");
        assertThat(metadata.properties().get(1).name()).isEqualTo("version");

        // Check inputs
        assertThat(metadata.inputs()).hasSize(3);
        assertThat(metadata.inputs().get(0).name()).isEqualTo("MagicNumber");
        assertThat(metadata.inputs().get(0).modifier()).isEqualTo(VariableDeclaration.Modifier.INPUT);
        assertThat(metadata.inputs().get(2).name()).isEqualTo("SymbolName");
        assertThat(metadata.inputs().get(2).modifier()).isEqualTo(VariableDeclaration.Modifier.EXTERN);
    }

    @Test
    @DisplayName("Given a non-existent file, the processor should throw an exception")
    void givenNonExistentFile_whenProcess_thenThrowsException() {
        ArtifactProcessingQueue item = new ArtifactProcessingQueue();
        item.setFilePath("non/existent/path.mq5");

        ArtifactItemProcessor processor = new ArtifactItemProcessor();
        
        try {
            processor.process(item);
        } catch (Exception e) {
            assertThat(e).isInstanceOf(java.nio.file.NoSuchFileException.class);
        }
    }
}
