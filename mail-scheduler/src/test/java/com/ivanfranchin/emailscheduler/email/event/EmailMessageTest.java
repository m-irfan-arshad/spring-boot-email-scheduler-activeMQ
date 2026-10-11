package com.ivanfranchin.emailscheduler.email.event;

import static org.assertj.core.api.Assertions.assertThat;

import com.ivanfranchin.emailscheduler.email.dto.CreateEmailRequest;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class EmailMessageTest {

  @Test
  void from_shouldCreateEmailMessageWithCorrectFields() {
    CreateEmailRequest createEmailRequest =
        new CreateEmailRequest("test@example.com", "Subject", "Body", 5000L);
    Instant before = Instant.now();
    UUID id = UUID.randomUUID();

    EmailMessage emailMessage = EmailMessage.from(createEmailRequest, id);

    Instant after = Instant.now();
    assertThat(emailMessage.id()).isEqualTo(id.toString());
    assertThat(emailMessage.to()).isEqualTo("test@example.com");
    assertThat(emailMessage.subject()).isEqualTo("Subject");
    assertThat(emailMessage.body()).isEqualTo("Body");
    assertThat(emailMessage.delay()).isEqualTo(Duration.ofMillis(5000));
    assertThat(emailMessage.createdAt()).isBetween(before, after.plusMillis(1));
    assertThat(emailMessage.expectedReturnTime())
        .isBetween(before.plusMillis(5000), after.plusMillis(5001));
  }

  @Test
  void from_nullSubjectAndBody_shouldDefaultToEmptyString() {
    CreateEmailRequest createEmailRequest =
        new CreateEmailRequest("test@example.com", null, null, 1000L);
    UUID id = UUID.randomUUID();

    EmailMessage emailMessage = EmailMessage.from(createEmailRequest, id);

    assertThat(emailMessage.subject()).isEmpty();
    assertThat(emailMessage.body()).isEmpty();
  }

  @Test
  void from_zeroDelay_shouldSetExpectedReturnTimeEqualToCreatedAt() {
    CreateEmailRequest createEmailRequest =
        new CreateEmailRequest("test@example.com", "Subject", "Body", 0L);
    UUID id = UUID.randomUUID();

    EmailMessage emailMessage = EmailMessage.from(createEmailRequest, id);

    assertThat(emailMessage.delay()).isEqualTo(Duration.ofMillis(0));
    assertThat(emailMessage.expectedReturnTime()).isEqualTo(emailMessage.createdAt());
  }
}
