package com.algocd.webportal.services;

import com.algocd.webportal.entities.ArtifactProcessingQueue;
import com.algocd.webportal.mappers.ArtifactProcessingQueueMapper;
import com.algocd.webportal.services.pipeline.ArtifactPipelineGateway;
import com.algocd.webportal.util.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ArtifactServiceImplTest {

    private StorageService storageService;
    private ArtifactProcessingQueueMapper queueMapper;
    private ArtifactPipelineGateway gateway;
    private ArtifactServiceImpl artifactService;

    @BeforeEach
    void setUp() {
        storageService = mock(StorageService.class);
        queueMapper = mock(ArtifactProcessingQueueMapper.class);
        gateway = mock(ArtifactPipelineGateway.class);
        artifactService = new ArtifactServiceImpl(storageService, queueMapper, gateway);
    }

    @Test
    void processAndQueueArtifacts_ShouldSetSameBatchIdForMultipleFiles() {
        UUID userId = UUID.randomUUID();
        MultipartFile file1 = mock(MultipartFile.class);
        MultipartFile file2 = mock(MultipartFile.class);

        when(file1.isEmpty()).thenReturn(false);
        when(file1.getOriginalFilename()).thenReturn("file1.mq5");
        when(file2.isEmpty()).thenReturn(false);
        when(file2.getOriginalFilename()).thenReturn("file2.mq5");

        when(storageService.store(any())).thenReturn(Result.success(Path.of("test/path")));

        artifactService.processAndQueueArtifacts(new MultipartFile[]{file1, file2}, userId);

        ArgumentCaptor<ArtifactProcessingQueue> captor = ArgumentCaptor.forClass(ArtifactProcessingQueue.class);
        verify(queueMapper, times(2)).insert(captor.capture());

        List<ArtifactProcessingQueue> records = captor.getAllValues();
        assertEquals(2, records.size());
        assertNotNull(records.get(0).getBatchId());
        assertEquals(records.get(0).getBatchId(), records.get(1).getBatchId());
    }
}
