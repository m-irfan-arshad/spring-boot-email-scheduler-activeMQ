package com.ivanfranchin.emailscheduler.email;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.ivanfranchin.emailscheduler.email.dto.CreateEmailRequest;
import com.ivanfranchin.emailscheduler.email.event.EmailMessage;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@WebMvcTest(EmailController.class)
class EmailControllerTest {

  @MockitoBean private EmailService emailService;

  @Autowired private EmailController emailController;

  @Test
  void createEmail_shouldReturnEmailMessage() {
    CreateEmailRequest request =
        new CreateEmailRequest("test@example.com", "Subject", "Body", 1000L);
    String id = UUID.randomUUID().toString();

    EmailMessage emailMessage =
        new EmailMessage(
            id,
            "test@example.com",
            "Subject",
            "Body",
            Duration.ofMillis(1000),
            Instant.now(),
            Instant.now().plusMillis(1000));

    when(emailService.createEmail(any(CreateEmailRequest.class))).thenReturn(emailMessage);

    EmailMessage result = emailController.createEmail(request);

    assertThat(result).isNotNull();
    assertThat(result.id()).isEqualTo(id);
    assertThat(result.to()).isEqualTo("test@example.com");
    assertThat(result.subject()).isEqualTo("Subject");
    assertThat(result.body()).isEqualTo("Body");
  }
}
