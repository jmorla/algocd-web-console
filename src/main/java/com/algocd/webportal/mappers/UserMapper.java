package com.algocd.webportal.mappers;

import com.algocd.webportal.entities.User;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Mapper
public interface UserMapper {

    @Insert("""
        INSERT INTO users (
            user_id, username, email, password_hash, onboarding_completed, 
            enabled, account_non_expired, account_non_locked, 
            credentials_non_expired, created_at
        ) VALUES (
            #{userId, jdbcType=OTHER}, #{username}, #{email}, #{passwordHash}, #{onboardingCompleted}, 
            #{enabled}, #{accountNonExpired}, #{accountNonLocked}, 
            #{credentialsNonExpired}, #{createdAt}
        )
        """)
    void insert(User user);

    @Select("SELECT * FROM users WHERE user_id = #{userId, jdbcType=OTHER}")
    @Results(id = "userResultMap", value = {
        @Result(property = "userId", column = "user_id", id = true, jdbcType = JdbcType.OTHER),
        @Result(property = "username", column = "username"),
        @Result(property = "email", column = "email"),
        @Result(property = "passwordHash", column = "password_hash"),
        @Result(property = "onboardingCompleted", column = "onboarding_completed"),
        @Result(property = "enabled", column = "enabled"),
        @Result(property = "accountNonExpired", column = "account_non_expired"),
        @Result(property = "accountNonLocked", column = "account_non_locked"),
        @Result(property = "credentialsNonExpired", column = "credentials_non_expired"),
        @Result(property = "createdAt", column = "created_at")
    })
    Optional<User> findById(UUID userId);

    @Select("SELECT * FROM users WHERE username = #{username}")
    @ResultMap("userResultMap")
    Optional<User> findByUsername(String username);

    @Select("SELECT * FROM users WHERE username = #{identifier} OR email = #{identifier}")
    @ResultMap("userResultMap")
    Optional<User> findByUsernameOrEmail(String identifier);

    @Select("SELECT * FROM users")
    @ResultMap("userResultMap")
    List<User> findAll();

    @Select("SELECT COUNT(*) > 0 FROM users WHERE email = #{email}")
    boolean existsByEmail(String email);

    @Update("""
        UPDATE users SET 
            username = #{username}, 
            email = #{email}, 
            password_hash = #{passwordHash}, 
            onboarding_completed = #{onboardingCompleted}, 
            enabled = #{enabled}, 
            account_non_expired = #{accountNonExpired}, 
            account_non_locked = #{accountNonLocked}, 
            credentials_non_expired = #{credentialsNonExpired}
        WHERE user_id = #{userId, jdbcType=OTHER}
        """)
    void update(User user);

    @Delete("DELETE FROM users WHERE user_id = #{userId, jdbcType=OTHER}")
    void deleteById(UUID userId);
}
