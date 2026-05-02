package com.algocd.webportal.mappers;

import com.algocd.webportal.entities.Terminal;
import com.algocd.webportal.entities.TerminalSummary;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.UUID;

@Mapper
public interface TerminalMapper {

    @Insert("""
        INSERT INTO terminals (
            terminal_id, user_id, status, instance_ip, 
            bootstrap_token, bootstrap_token_expires_at, created_at, updated_at
        ) VALUES (
            #{terminalId, jdbcType=OTHER}, #{userId, jdbcType=OTHER}, 
            #{status}, #{instanceIp}, #{bootstrapToken}, #{bootstrapTokenExpiresAt}, 
            #{createdAt}, #{updatedAt}
        )
        """)
    void insert(Terminal terminal);

    @Select("""
        SELECT 
            t.terminal_id, 
            tag_name.tag_value as name, 
            tag_platform.tag_value as platform, 
            t.status, 
            t.instance_ip, 
            t.created_at, 
            t.updated_at
        FROM terminals t
        LEFT JOIN tags tag_name ON tag_name.resource_id = CAST(t.terminal_id AS VARCHAR) AND tag_name.tag_key = 'name'
        LEFT JOIN tags tag_platform ON tag_platform.resource_id = CAST(t.terminal_id AS VARCHAR) AND tag_platform.tag_key = 'platform'
        WHERE t.user_id = #{userId, jdbcType=OTHER}
        ORDER BY t.created_at DESC
        LIMIT #{limit} OFFSET #{offset}
        """)
    List<TerminalSummary> findTerminalsByUserId(@Param("userId") UUID userId, @Param("limit") int limit, @Param("offset") int offset);

    @Select("SELECT COUNT(*) FROM terminals WHERE user_id = #{userId, jdbcType=OTHER}")
    long countTerminalsByUserId(@Param("userId") UUID userId);

    @Update("""
        UPDATE terminals 
        SET status = #{status}, updated_at = #{updatedAt} 
        WHERE terminal_id = #{terminalId, jdbcType=OTHER}
        """)
    void updateStatus(@Param("terminalId") UUID terminalId, @Param("status") com.algocd.webportal.entities.TerminalStatus status, @Param("updatedAt") java.time.Instant updatedAt);

    @Update("""
        UPDATE terminals 
        SET status = #{status}, instance_ip = #{instanceIp}, updated_at = #{updatedAt} 
        WHERE terminal_id = #{terminalId, jdbcType=OTHER}
        """)
    void updateStatusWithIp(@Param("terminalId") UUID terminalId, @Param("status") com.algocd.webportal.entities.TerminalStatus status, @Param("instanceIp") String instanceIp, @Param("updatedAt") java.time.Instant updatedAt);

    @Select("""
        SELECT terminal_id, user_id, status, instance_ip, bootstrap_token, bootstrap_token_expires_at, created_at, updated_at
        FROM terminals
        WHERE bootstrap_token = #{bootstrapToken}
        """)
    Terminal findByBootstrapToken(@Param("bootstrapToken") String bootstrapToken);

    @Update("""
        UPDATE terminals
        SET status = #{status}, 
            instance_ip = #{instanceIp},
            bootstrap_token = NULL,
            bootstrap_token_expires_at = NULL,
            updated_at = #{updatedAt}
        WHERE terminal_id = #{terminalId, jdbcType=OTHER}
        """)
    void completeBootstrap(
        @Param("terminalId") UUID terminalId,
        @Param("status") com.algocd.webportal.entities.TerminalStatus status,
        @Param("instanceIp") String instanceIp,
        @Param("updatedAt") java.time.Instant updatedAt
    );
}
