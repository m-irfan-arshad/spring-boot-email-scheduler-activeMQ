package com.ivanfranchin.emailscheduler.email.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record CreateEmailRequest(
    @NotBlank @Email String to,
    String subject,
    String body,
    @NotNull @PositiveOrZero Long delayInMillis) {}
