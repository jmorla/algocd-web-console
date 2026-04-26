package com.algocd.webportal.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            return false;
        }

        boolean hasMinLength = password.length() >= 8;
        boolean hasUppercase = password.chars().anyMatch(Character::isUpperCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecial = password.matches(".*[^A-Za-z0-9].*");

        return hasMinLength && hasUppercase && hasDigit && hasSpecial;
    }
}
