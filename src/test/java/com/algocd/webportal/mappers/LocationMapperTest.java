package com.algocd.webportal.mappers;

import com.algocd.webportal.TestcontainersConfiguration;
import com.algocd.webportal.entities.Location;
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
class LocationMapperTest {

    @Autowired
    private LocationMapper locationMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("Given locations in the database, when findAll is called, then all locations are returned")
    void givenLocations_whenFindAll_thenReturnsAllLocations() {
        // Given
        UUID loc1Id = UUID.randomUUID();
        UUID loc2Id = UUID.randomUUID();
        jdbcTemplate.execute(String.format("INSERT INTO locations (location_id, name, region, enabled) VALUES ('%s', 'New York', 'US East', true)", loc1Id));
        jdbcTemplate.execute(String.format("INSERT INTO locations (location_id, name, region, enabled) VALUES ('%s', 'London', 'Europe', false)", loc2Id));

        // When
        List<Location> locations = locationMapper.findAll();

        // Then
        assertThat(locations).hasSizeGreaterThanOrEqualTo(2);
        assertThat(locations).extracting(Location::getLocationId).contains(loc1Id, loc2Id);
        
        Location loc1 = locations.stream().filter(l -> l.getLocationId().equals(loc1Id)).findFirst().orElseThrow();
        assertThat(loc1.getName()).isEqualTo("New York");
        assertThat(loc1.getRegion()).isEqualTo("US East");
        assertThat(loc1.isEnabled()).isTrue();
    }
}
