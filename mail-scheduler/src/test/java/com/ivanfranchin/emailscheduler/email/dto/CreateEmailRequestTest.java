package com.ivanfranchin.emailscheduler.email.dto;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CreateEmailRequestTest {

  @Test
  void constructor_shouldCreateValidRequest() {
    CreateEmailRequest request =
        new CreateEmailRequest("test@example.com", "Subject", "Body", 1000L);

    assertThat(request.to()).isEqualTo("test@example.com");
    assertThat(request.subject()).isEqualTo("Subject");
    assertThat(request.body()).isEqualTo("Body");
    assertThat(request.delayInMillis()).isEqualTo(1000L);
  }

  @Test
  void validation_emptyTo_shouldFail() {
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    CreateEmailRequest request = new CreateEmailRequest("", "Subject", "Body", 1000L);

    Set<ConstraintViolation<CreateEmailRequest>> violations = validator.validate(request);

    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("to");
  }

  @Test
  void validation_invalidEmail_shouldFail() {
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    CreateEmailRequest request = new CreateEmailRequest("not-an-email", "Subject", "Body", 1000L);

    Set<ConstraintViolation<CreateEmailRequest>> violations = validator.validate(request);

    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("to");
  }

  @Test
  void validation_negativeDelay_shouldFail() {
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    CreateEmailRequest request = new CreateEmailRequest("test@example.com", "Subject", "Body", -1L);

    Set<ConstraintViolation<CreateEmailRequest>> violations = validator.validate(request);

    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getPropertyPath().toString())
        .isEqualTo("delayInMillis");
  }

  @Test
  void validation_nullDelay_shouldFail() {
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    CreateEmailRequest request =
        new CreateEmailRequest("test@example.com", "Subject", "Body", null);

    Set<ConstraintViolation<CreateEmailRequest>> violations = validator.validate(request);

    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getPropertyPath().toString())
        .isEqualTo("delayInMillis");
  }
}
