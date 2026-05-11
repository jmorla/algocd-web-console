package com.algocd.webportal.mappers;

import com.algocd.webportal.entities.ArtifactProcessingQueue;
import org.apache.ibatis.annotations.*;

import java.util.UUID;

@Mapper
public interface ArtifactProcessingQueueMapper {

    @Insert("""
        INSERT INTO artifacts_processing_queue (
            user_id, file_path, original_filename, status, message, digest, created_at, updated_at
        ) VALUES (
            #{userId, jdbcType=OTHER},
            #{filePath},
            #{originalFilename},
            #{status},
            #{message},
            #{digest},
            #{createdAt},
            #{updatedAt}
        )
        """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(ArtifactProcessingQueue record);

    @Select("""
        SELECT id, user_id, file_path, original_filename, status, message, digest, created_at, updated_at
        FROM artifacts_processing_queue
        WHERE user_id = #{userId, jdbcType=OTHER}
        ORDER BY created_at ASC
        """)
    org.apache.ibatis.cursor.Cursor<ArtifactProcessingQueue> findByUserIdCursor(@Param("userId") UUID userId);

    @Update("""
        UPDATE artifacts_processing_queue
        SET status = #{status},
            message = #{message},
            updated_at = #{updatedAt}
        WHERE id = #{id}
        """)
    void updateStatus(
            @Param("id") Long id,
            @Param("status") com.algocd.webportal.entities.ProcessingStatus status,
            @Param("message") String message,
            @Param("updatedAt") java.time.Instant updatedAt
    );

    @Delete("""
        DELETE FROM artifacts_processing_queue
        WHERE id = #{id}
        """)
    void deleteById(Long id);
}
