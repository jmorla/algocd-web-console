package com.algocd.webportal.validation;

import com.algocd.webportal.mappers.UserMapper;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {

    private UserMapper userMapper;

    public UniqueEmailValidator() {
    }

    public UniqueEmailValidator(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || userMapper == null) {
            return true;
        }
        return !userMapper.existsByEmail(email);
    }
}
