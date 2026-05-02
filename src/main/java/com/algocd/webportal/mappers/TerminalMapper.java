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
            terminal_id, user_id, name, version, status, instance_ip, created_at, updated_at
        ) VALUES (
            #{terminalId, jdbcType=OTHER}, #{userId, jdbcType=OTHER}, #{name}, #{version}, 
            #{status}, #{instanceIp}, #{createdAt}, #{updatedAt}
        )
        """)
    void insert(Terminal terminal);

    @Select("""
        SELECT 
            terminal_id, name, version, status, instance_ip, created_at, updated_at
        FROM terminals
        WHERE user_id = #{userId, jdbcType=OTHER}
        ORDER BY created_at DESC
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
    void updateStatus(@Param("terminalId") UUID terminalId, @Param("status") String status, @Param("updatedAt") java.time.Instant updatedAt);

    @Update("""
        UPDATE terminals 
        SET status = #{status}, instance_ip = #{instanceIp}, updated_at = #{updatedAt} 
        WHERE terminal_id = #{terminalId, jdbcType=OTHER}
        """)
    void updateStatusWithIp(@Param("terminalId") UUID terminalId, @Param("status") String status, @Param("instanceIp") String instanceIp, @Param("updatedAt") java.time.Instant updatedAt);
}
