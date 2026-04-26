package com.algocd.webportal.mappers;

import com.algocd.webportal.TestcontainersConfiguration;
import com.algocd.webportal.entities.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@MybatisTest
@Import(TestcontainersConfiguration.class)
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    @DisplayName("Given a new user, when inserting, then the user should be found by ID")
    void givenNewUser_whenInsert_thenCanBeFoundById() {
        UUID userId = UUID.randomUUID();
        User user = new User(
            userId,
            "testuser",
            "test@example.com",
            "hashedpassword",
            false,
            true,
            true,
            true,
            true,
            Instant.now()
        );

        userMapper.insert(user);

        Optional<User> found = userMapper.findById(userId);
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("testuser");
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("Given an existing user, when updating their onboarding status, then the changes should be persisted")
    void givenExistingUser_whenUpdateOnboarding_thenStatusIsUpdated() {
        UUID userId = UUID.randomUUID();
        User user = new User(
            userId,
            "updateuser",
            "update@example.com",
            "hashedpassword",
            false,
            true,
            true,
            true,
            true,
            Instant.now()
        );
        userMapper.insert(user);

        user.setOnboardingCompleted(true);
        userMapper.update(user);

        Optional<User> found = userMapper.findById(userId);
        assertThat(found).isPresent();
        assertThat(found.get().isOnboardingCompleted()).isTrue();
    }

    @Test
    @DisplayName("Given an existing user, when searching by username or email, then the user should be found")
    void givenExistingUser_whenFindByUsernameOrEmail_thenUserIsFound() {
        UUID userId = UUID.randomUUID();
        String username = "searchuser";
        String email = "search@example.com";
        User user = new User(
            userId,
            username,
            email,
            "hashedpassword",
            false,
            true,
            true,
            true,
            true,
            Instant.now()
        );
        userMapper.insert(user);

        // Search by username
        Optional<User> foundByUsername = userMapper.findByUsernameOrEmail(username);
        assertThat(foundByUsername).isPresent();
        assertThat(foundByUsername.get().getUserId()).isEqualTo(userId);

        // Search by email
        Optional<User> foundByEmail = userMapper.findByUsernameOrEmail(email);
        assertThat(foundByEmail).isPresent();
        assertThat(foundByEmail.get().getUserId()).isEqualTo(userId);

        // Search by non-existent
        Optional<User> notFound = userMapper.findByUsernameOrEmail("nonexistent");
        assertThat(notFound).isEmpty();
    }

    @Test
    @DisplayName("Given an existing user, when deleting by ID, then the user should no longer exist")
    void givenExistingUser_whenDeleteById_thenUserIsRemoved() {
        UUID userId = UUID.randomUUID();
        User user = new User(
            userId,
            "deleteuser",
            "delete@example.com",
            "hashedpassword",
            false,
            true,
            true,
            true,
            true,
            Instant.now()
        );
        userMapper.insert(user);

        userMapper.deleteById(userId);

        Optional<User> found = userMapper.findById(userId);
        assertThat(found).isEmpty();
    }
}
