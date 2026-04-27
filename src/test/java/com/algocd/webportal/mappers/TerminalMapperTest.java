package com.algocd.webportal.mappers;

import com.algocd.webportal.TestcontainersConfiguration;
import com.algocd.webportal.entities.MetaTraderVersion;
import com.algocd.webportal.entities.TerminalSummary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@MybatisTest
@Import(TestcontainersConfiguration.class)
class TerminalMapperTest {

    @Autowired
    private TerminalMapper terminalMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final UUID userId = UUID.randomUUID();
    private final UUID planId = UUID.randomUUID();
    private final UUID locationId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        // Setup prerequisite data
        jdbcTemplate.update("INSERT INTO users (user_id, username, email, password_hash) VALUES (?, ?, ?, ?)",
                userId, "testuser", "test@example.com", "hash");

        jdbcTemplate.update("INSERT INTO plans (plan_id, name, cpu_cores, ram_gb, monthly_price, hourly_price, expert_limit) VALUES (?, ?, ?, ?, ?, ?, ?)",
                planId, "Pro Plan", 4, 8, 29.99, 0.05, 10);

        jdbcTemplate.update("INSERT INTO locations (location_id, name, region, enabled) VALUES (?, ?, ?, ?)",
                locationId, "New York", "us-east-1", true);
    }

    @Test
    @DisplayName("Given terminals exist, when findTerminalsByUserId is called, then it returns summaries with joined data")
    void givenTerminals_whenFindTerminalsByUserId_thenReturnsSummaries() {
        // Given
        UUID terminalId = UUID.randomUUID();
        jdbcTemplate.update("""
            INSERT INTO terminals (terminal_id, user_id, name, version, plan_id, location_id, status, instance_ip)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """, terminalId, userId, "Terminal 1", "METATRADER_5", planId, locationId, "RUNNING", "192.168.1.1");

        // When
        List<TerminalSummary> summaries = terminalMapper.findTerminalsByUserId(userId, 10, 0);

        // Then
        assertThat(summaries).hasSize(1);
        TerminalSummary summary = summaries.get(0);
        assertThat(summary.getTerminalId()).isEqualTo(terminalId);
        assertThat(summary.getName()).isEqualTo("Terminal 1");
        assertThat(summary.getVersion()).isEqualTo(MetaTraderVersion.METATRADER_5);
        assertThat(summary.getInstanceIp()).isEqualTo("192.168.1.1");
        
        // Verify Joins (Flat fields)
        assertThat(summary.getPlanName()).isEqualTo("Pro Plan");
        assertThat(summary.getCpuCores()).isEqualTo(4);
        assertThat(summary.getRamGb()).isEqualTo(8);
        
        assertThat(summary.getLocationName()).isEqualTo("New York");
        assertThat(summary.getLocationRegion()).isEqualTo("us-east-1");
    }

    @Test
    @DisplayName("Given multiple terminals, when findTerminalsByUserId is called with pagination, then it respects limit and offset")
    void givenMultipleTerminals_whenFindWithPagination_thenRespectsLimitAndOffset() {
        // Given
        for (int i = 0; i < 5; i++) {
            jdbcTemplate.update("""
                INSERT INTO terminals (terminal_id, user_id, name, version, plan_id, location_id, status, created_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP + (? * interval '1 minute'))
                """, UUID.randomUUID(), userId, "Terminal " + i, "METATRADER_4", planId, locationId, "RUNNING", i);
        }

        // When
        List<TerminalSummary> firstPage = terminalMapper.findTerminalsByUserId(userId, 2, 0);
        List<TerminalSummary> secondPage = terminalMapper.findTerminalsByUserId(userId, 2, 2);

        // Then
        assertThat(firstPage).hasSize(2);
        assertThat(secondPage).hasSize(2);
        assertThat(firstPage).extracting(TerminalSummary::getName).doesNotContainAnyElementsOf(
                secondPage.stream().map(TerminalSummary::getName).toList()
        );
    }
}
