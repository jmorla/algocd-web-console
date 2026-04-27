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
            terminal_id, user_id, name, version, plan_id, location_id, status, instance_ip, created_at, updated_at
        ) VALUES (
            #{terminalId, jdbcType=OTHER}, #{userId, jdbcType=OTHER}, #{name}, #{version}, 
            #{planId, jdbcType=OTHER}, #{locationId, jdbcType=OTHER}, #{status}, #{instanceIp}, #{createdAt}, #{updatedAt}
        )
        """)
    void insert(Terminal terminal);

    @Select("""
        SELECT 
            t.terminal_id, t.name, t.version, t.status, t.instance_ip, t.created_at, t.updated_at,
            p.name AS plan_name, p.cpu_cores, p.ram_gb, p.monthly_price, p.hourly_price, p.expert_limit,
            l.name AS location_name, l.region AS location_region
        FROM terminals t
        LEFT JOIN plans p ON t.plan_id = p.plan_id
        LEFT JOIN locations l ON t.location_id = l.location_id
        WHERE t.user_id = #{userId, jdbcType=OTHER}
        ORDER BY t.created_at DESC
        LIMIT #{limit} OFFSET #{offset}
        """)
    List<TerminalSummary> findTerminalsByUserId(@Param("userId") UUID userId, @Param("limit") int limit, @Param("offset") int offset);

    @Select("SELECT COUNT(*) FROM terminals WHERE user_id = #{userId, jdbcType=OTHER}")
    long countTerminalsByUserId(@Param("userId") UUID userId);
}
