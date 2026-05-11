package com.algocd.webportal.batch;

import com.algocd.webportal.entities.ArtifactProcessingQueue;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisCursorItemReader;
import java.util.Map;
import java.util.UUID;

/**
 * Custom batch reader for ArtifactProcessingQueue entities.
 * Uses MyBatisCursorItemReader to efficiently stream records for a specific user.
 */
public class ArtifactBatchReader extends MyBatisCursorItemReader<ArtifactProcessingQueue> {

    public ArtifactBatchReader(SqlSessionFactory sqlSessionFactory, UUID userId) {
        setSqlSessionFactory(sqlSessionFactory);
        setQueryId("com.algocd.webportal.mappers.ArtifactProcessingQueueMapper.findByUserIdCursor");
        setParameterValues(Map.of("userId", userId));
        // Disable state saving for simpler usage if not part of a restartable batch job
        setSaveState(false);
        setName("artifactProcessingBatchReader");
    }
}
