package com.algocd.webportal.validation;

import com.algocd.webportal.services.models.CreateUserRecord;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, CreateUserRecord> {

    @Override
    public boolean isValid(CreateUserRecord record, ConstraintValidatorContext context) {
        if (record == null) {
            return true;
        }

        boolean isValid = record.password() != null && record.password().equals(record.confirmPassword());

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("confirmPassword")
                    .addConstraintViolation();
        }

        return isValid;
    }
}
