package com.algocd.webportal.services;

import com.algocd.webportal.entities.User;
import com.algocd.webportal.mappers.UserMapper;
import com.algocd.webportal.services.models.CreateUserRecord;
import com.algocd.webportal.util.Result;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

@Service
@Validated
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final Validator validator;

    public UserServiceImpl(UserMapper userMapper, PasswordEncoder passwordEncoder, Validator validator) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.validator = validator;
    }

    @Override
    public Result<User> createUser(CreateUserRecord createUserRecord) {
        Set<ConstraintViolation<CreateUserRecord>> violations = validator.validate(createUserRecord);
        if (!violations.isEmpty()) {
            return Result.failure(new ConstraintViolationException(violations));
        }

        User user = new User();
        user.setUserId(UUID.randomUUID());
        user.setEmail(createUserRecord.email());
        user.setPasswordHash(passwordEncoder.encode(createUserRecord.password()));
        user.setUsername(null); // username can be null for now
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setOnboardingCompleted(false);
        user.setCreatedAt(Instant.now());

        userMapper.insert(user);
        return Result.success(user);
    }

    @Override
    public UserDetails loadUserByUsername(@NonNull String identifier) throws UsernameNotFoundException {
        User user = userMapper.findByUsernameOrEmail(identifier)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with identifier: " + identifier));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername() != null ? user.getUsername() : user.getEmail(),
                user.getPasswordHash(),
                user.isEnabled(),
                user.isAccountNonExpired(),
                user.isCredentialsNonExpired(),
                user.isAccountNonLocked(),
                new ArrayList<>() // Empty authorities for now
        );
    }
}
