package com.algocd.webportal.batch;

import com.algocd.webportal.TestcontainersConfiguration;
import com.algocd.webportal.entities.ArtifactProcessingQueue;
import com.algocd.webportal.entities.ProcessingStatus;
import com.algocd.webportal.mappers.ArtifactProcessingQueueMapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.batch.infrastructure.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@MybatisTest
@Import(TestcontainersConfiguration.class)
class ArtifactBatchReaderTest {

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Autowired
    private ArtifactProcessingQueueMapper mapper;

    @Test
    @DisplayName("Given multiple records for a user, the batch reader should return all of them")
    void givenRecords_whenRead_thenAllReturned() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();
        
        for (int i = 0; i < 5; i++) {
            ArtifactProcessingQueue record = new ArtifactProcessingQueue();
            record.setUserId(userId);
            record.setFilePath("path/to/file" + i);
            record.setOriginalFilename("file" + i + ".mq5");
            record.setStatus(ProcessingStatus.QUEUED);
            record.setDigest("hash" + i);
            record.setCreatedAt(Instant.now());
            record.setUpdatedAt(Instant.now());
            mapper.insert(record);
        }
        
        // One record for another user to ensure filtering works
        ArtifactProcessingQueue otherRecord = new ArtifactProcessingQueue();
        otherRecord.setUserId(otherUserId);
        otherRecord.setFilePath("path/other");
        otherRecord.setOriginalFilename("other.mq5");
        otherRecord.setStatus(ProcessingStatus.QUEUED);
        otherRecord.setCreatedAt(Instant.now());
        otherRecord.setUpdatedAt(Instant.now());
        mapper.insert(otherRecord);

        ArtifactBatchReader reader = new ArtifactBatchReader(sqlSessionFactory, userId);
        reader.open(new ExecutionContext());
        
        List<ArtifactProcessingQueue> results = new ArrayList<>();
        ArtifactProcessingQueue item;
        while ((item = reader.read()) != null) {
            results.add(item);
        }
        
        reader.close();
        
        assertThat(results).hasSize(5);
        assertThat(results).allMatch(r -> r.getUserId().equals(userId));
    }

    @Test
    @DisplayName("Given no records for a user, the batch reader should return null on first read")
    void givenNoRecords_whenRead_thenReturnsNull() throws Exception {
        UUID userId = UUID.randomUUID();
        
        ArtifactBatchReader reader = new ArtifactBatchReader(sqlSessionFactory, userId);
        reader.open(new ExecutionContext());
        
        assertThat(reader.read()).isNull();
        
        reader.close();
    }
}
