package com.algocd.webportal.services.models;

import com.algocd.webportal.validation.PasswordMatch;
import com.algocd.webportal.validation.UniqueEmail;
import com.algocd.webportal.validation.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@PasswordMatch(message = "{register.form.error.password.mismatch}")
public record CreateUserRecord(
    @NotBlank(message = "{register.form.error.email.required}")
    @Email(message = "{register.form.error.email.invalid}")
    @UniqueEmail(message = "{register.form.error.email.exists}")
    String email,

    @NotBlank(message = "{register.form.error.password.required}")
    @ValidPassword(message = "{register.form.error.password.invalid}")
    String password,

    @NotBlank(message = "{register.form.error.password.confirm.required}")
    String confirmPassword
) {
}
