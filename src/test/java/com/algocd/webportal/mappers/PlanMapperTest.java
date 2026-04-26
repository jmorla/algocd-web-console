package com.algocd.webportal.mappers;

import com.algocd.webportal.TestcontainersConfiguration;
import com.algocd.webportal.entities.Plan;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@MybatisTest
@Import(TestcontainersConfiguration.class)
class PlanMapperTest {

    @Autowired
    private PlanMapper planMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("Given plans in the database, when findAll is called, then all plans are returned")
    void givenPlans_whenFindAll_thenReturnsAllPlans() {
        // Given
        jdbcTemplate.execute("""
            INSERT INTO plans (plan_id, name, cpu_cores, ram_gb, monthly_price, hourly_price, expert_limit)
            VALUES ('plan1', 'Basic Plan', 1, 2, 10.00, 0.015, 5)
            """);
        jdbcTemplate.execute("""
            INSERT INTO plans (plan_id, name, cpu_cores, ram_gb, monthly_price, hourly_price, expert_limit)
            VALUES ('plan2', 'Pro Plan', 2, 4, 20.00, 0.030, 10)
            """);

        // When
        List<Plan> plans = planMapper.findAll();

        // Then
        assertThat(plans).hasSizeGreaterThanOrEqualTo(2);
        assertThat(plans).extracting(Plan::getPlanId).contains("plan1", "plan2");
        
        Plan basicPlan = plans.stream().filter(p -> p.getPlanId().equals("plan1")).findFirst().orElseThrow();
        assertThat(basicPlan.getName()).isEqualTo("Basic Plan");
        assertThat(basicPlan.getCpuCores()).isEqualTo(1);
        assertThat(basicPlan.getRamGb()).isEqualTo(2);
        assertThat(basicPlan.getMonthlyPrice()).isEqualByComparingTo("10.00");
        assertThat(basicPlan.getHourlyPrice()).isEqualByComparingTo("0.015");
        assertThat(basicPlan.getExpertLimit()).isEqualTo(5);
    }
}
