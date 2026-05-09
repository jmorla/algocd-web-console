package com.algocd.webportal.mappers;

import com.algocd.webportal.entities.Artifact;
import com.algocd.webportal.entities.ArtifactType;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.UUID;

@Mapper
public interface ArtifactMapper {

    @Insert("""
        INSERT INTO artifacts (
            artifact_id, name, digest, type, size_bytes, platform, version, filename, created_at
        ) VALUES (
            #{artifactId, jdbcType=OTHER},
            #{name}, #{digest}, #{type}, #{sizeBytes}, #{platform}, #{version}, #{filename}, #{createdAt}
        )
        """)
    void insert(Artifact artifact);

    @Select("""
        SELECT artifact_id, name, digest, type, size_bytes, platform, version, filename, created_at
        FROM artifacts
        WHERE type = #{type}
        ORDER BY created_at DESC
        LIMIT #{limit} OFFSET #{offset}
        """)
    List<Artifact> findArtifactsByType(
            @Param("type") ArtifactType type, 
            @Param("limit") int limit, 
            @Param("offset") int offset
    );

    @Select("""
        SELECT COUNT(*)
        FROM artifacts
        WHERE type = #{type}
        """)
    long countArtifactsByType(@Param("type") ArtifactType type);
}
