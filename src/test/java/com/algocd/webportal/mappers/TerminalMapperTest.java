package com.algocd.webportal.mappers;

import com.algocd.webportal.TestcontainersConfiguration;
import com.algocd.webportal.entities.Platform;
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

    @BeforeEach
    void setUp() {
        // Setup prerequisite data
        jdbcTemplate.update("INSERT INTO users (user_id, username, email, password_hash) VALUES (?, ?, ?, ?)",
                userId, "testuser", "test@example.com", "hash");
    }

    @Test
    @DisplayName("Given terminals exist, when findTerminalsByUserId is called, then it returns summaries")
    void givenTerminals_whenFindTerminalsByUserId_thenReturnsSummaries() {
        // Given
        UUID terminalId = UUID.randomUUID();
        jdbcTemplate.update("""
            INSERT INTO terminals (terminal_id, user_id, status, instance_ip)
            VALUES (?, ?, ?, ?)
            """, terminalId, userId, "CONNECTED", "192.168.1.1");
        
        jdbcTemplate.update("""
            INSERT INTO tags (resource_id, tag_key, tag_value)
            VALUES (?, ?, ?)
            """, terminalId.toString(), "name", "Terminal 1");

        jdbcTemplate.update("""
            INSERT INTO tags (resource_id, tag_key, tag_value)
            VALUES (?, ?, ?)
            """, terminalId.toString(), "platform", "METATRADER_5");

        // When
        List<TerminalSummary> summaries = terminalMapper.findTerminalsByUserId(userId, 10, 0);

        // Then
        assertThat(summaries).hasSize(1);
        TerminalSummary summary = summaries.get(0);
        assertThat(summary.getTerminalId()).isEqualTo(terminalId);
        assertThat(summary.getName()).isEqualTo("Terminal 1");
        assertThat(summary.getPlatform()).isEqualTo(Platform.METATRADER_5);
        assertThat(summary.getInstanceIp()).isEqualTo("192.168.1.1");
    }

    @Test
    @DisplayName("Given multiple terminals, when findTerminalsByUserId is called with pagination, then it respects limit and offset")
    void givenMultipleTerminals_whenFindWithPagination_thenRespectsLimitAndOffset() {
        // Given
        for (int i = 0; i < 5; i++) {
            UUID terminalId = UUID.randomUUID();
            jdbcTemplate.update("""
                INSERT INTO terminals (terminal_id, user_id, status, created_at)
                VALUES (?, ?, ?, CURRENT_TIMESTAMP + (? * interval '1 minute'))
                """, terminalId, userId, "CONNECTED", i);
            
            jdbcTemplate.update("""
                INSERT INTO tags (resource_id, tag_key, tag_value)
                VALUES (?, ?, ?)
                """, terminalId.toString(), "name", "Terminal " + i);

            jdbcTemplate.update("""
                INSERT INTO tags (resource_id, tag_key, tag_value)
                VALUES (?, ?, ?)
                """, terminalId.toString(), "platform", "METATRADER_4");
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
