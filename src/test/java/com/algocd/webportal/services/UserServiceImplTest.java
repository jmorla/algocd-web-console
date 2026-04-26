package com.algocd.webportal.services;

import com.algocd.webportal.entities.User;
import com.algocd.webportal.mappers.UserMapper;
import com.algocd.webportal.services.models.CreateUserRecord;
import com.algocd.webportal.util.Result;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Validator validator;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User(
                UUID.randomUUID(),
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
    }

    @Test
    @DisplayName("Given valid CreateUserRecord, when creating user, then return Success Result")
    void givenValidCreateUserRecord_whenCreateUser_thenReturnSuccessResult() {
        CreateUserRecord record = new CreateUserRecord("new@example.com", "password123", "password123");
        when(validator.validate(record)).thenReturn(Collections.emptySet());
        when(passwordEncoder.encode(anyString())).thenReturn("hashed_new_password");

        Result<User> result = userService.createUser(record);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue().getEmail()).isEqualTo("new@example.com");
        verify(validator, times(1)).validate(record);
        verify(userMapper, times(1)).insert(any(User.class));
    }

    @Test
    @DisplayName("Given invalid CreateUserRecord, when creating user, then return Failure Result with ConstraintViolationException")
    void givenInvalidCreateUserRecord_whenCreateUser_thenReturnFailureResult() {
        CreateUserRecord record = new CreateUserRecord("invalid", "p", "p2");
        ConstraintViolation<CreateUserRecord> violation = mock(ConstraintViolation.class);
        when(validator.validate(record)).thenReturn(Set.of(violation));

        Result<User> result = userService.createUser(record);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError()).isInstanceOf(ConstraintViolationException.class);
        verify(userMapper, never()).insert(any(User.class));
    }

    @Test
    @DisplayName("Given user with null username, when loading user, then use email as username in UserDetails")
    void givenNullUsername_whenLoadUser_thenUseEmailAsUsername() {
        testUser.setUsername(null);
        when(userMapper.findByUsernameOrEmail("test@example.com")).thenReturn(Optional.of(testUser));

        UserDetails userDetails = userService.loadUserByUsername("test@example.com");

        assertThat(userDetails.getUsername()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("Given valid username, when loading user, then return UserDetails")
    void givenValidUsername_whenLoadUser_thenReturnUserDetails() {
        when(userMapper.findByUsernameOrEmail("testuser")).thenReturn(Optional.of(testUser));

        UserDetails userDetails = userService.loadUserByUsername("testuser");

        assertThat(userDetails.getUsername()).isEqualTo("testuser");
        assertThat(userDetails.getPassword()).isEqualTo("hashedpassword");
    }

    @Test
    @DisplayName("Given valid email, when loading user, then return UserDetails")
    void givenValidEmail_whenLoadUser_thenReturnUserDetails() {
        when(userMapper.findByUsernameOrEmail("test@example.com")).thenReturn(Optional.of(testUser));

        UserDetails userDetails = userService.loadUserByUsername("test@example.com");

        assertThat(userDetails.getUsername()).isEqualTo("testuser");
        assertThat(userDetails.getPassword()).isEqualTo("hashedpassword");
    }

    @Test
    @DisplayName("Given invalid identifier, when loading user, then throw UsernameNotFoundException")
    void givenInvalidIdentifier_whenLoadUser_thenThrowException() {
        when(userMapper.findByUsernameOrEmail(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.loadUserByUsername("invalid"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found");
    }
}
