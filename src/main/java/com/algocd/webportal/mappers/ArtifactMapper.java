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
            artifact_id, user_id, name, digest, type, size_bytes, platform, version, created_at
        ) VALUES (
            #{artifactId, jdbcType=OTHER}, #{userId, jdbcType=OTHER}, 
            #{name}, #{digest}, #{type}, #{sizeBytes}, #{platform}, #{version}, #{createdAt}
        )
        """)
    void insert(Artifact artifact);

    @Select("""
        SELECT artifact_id, user_id, name, digest, type, size_bytes, platform, version, created_at
        FROM artifacts
        WHERE user_id = #{userId, jdbcType=OTHER} AND type = #{type}
        ORDER BY created_at DESC
        LIMIT #{limit} OFFSET #{offset}
        """)
    List<Artifact> findArtifactsByUserIdAndType(
            @Param("userId") UUID userId, 
            @Param("type") ArtifactType type, 
            @Param("limit") int limit, 
            @Param("offset") int offset
    );

    @Select("""
        SELECT COUNT(*) 
        FROM artifacts 
        WHERE user_id = #{userId, jdbcType=OTHER} AND type = #{type}
        """)
    long countArtifactsByUserIdAndType(@Param("userId") UUID userId, @Param("type") ArtifactType type);
}
