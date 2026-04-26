package com.algocd.webportal.mappers;

import com.algocd.webportal.entities.Terminal;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TerminalMapper {

    @Insert("""
        INSERT INTO terminals (
            terminal_id, user_id, name, version, plan_id, location_id, status, created_at, updated_at
        ) VALUES (
            #{terminalId, jdbcType=OTHER}, #{userId, jdbcType=OTHER}, #{name}, #{version}, 
            #{planId, jdbcType=OTHER}, #{locationId, jdbcType=OTHER}, #{status}, #{createdAt}, #{updatedAt}
        )
        """)
    void insert(Terminal terminal);
}
