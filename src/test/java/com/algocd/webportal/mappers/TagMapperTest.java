package com.algocd.webportal.mappers;

import com.algocd.webportal.TestcontainersConfiguration;
import com.algocd.webportal.entities.Tag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@MybatisTest
@Import(TestcontainersConfiguration.class)
class TagMapperTest {

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("Given tags in the database, when findAll is called, then all tags are returned")
    void givenTags_whenFindAll_thenReturnsAllTags() {
        // Given
        jdbcTemplate.execute("INSERT INTO tags (resource_id, tag_key, tag_value) VALUES ('res1', 'key1', 'val1')");
        jdbcTemplate.execute("INSERT INTO tags (resource_id, tag_key, tag_value) VALUES ('res1', 'key2', 'val2')");

        // When
        List<Tag> tags = tagMapper.findAll();

        // Then
        assertThat(tags).hasSizeGreaterThanOrEqualTo(2);
        assertThat(tags).extracting(Tag::getTagKey).contains("key1", "key2");
    }
}
