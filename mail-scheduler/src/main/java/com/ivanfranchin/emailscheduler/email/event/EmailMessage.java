package com.ivanfranchin.emailscheduler.email.event;

import com.ivanfranchin.emailscheduler.email.dto.CreateEmailRequest;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record EmailMessage(
    String id,
    String to,
    String subject,
    String body,
    Duration delay,
    Instant createdAt,
    Instant expectedReturnTime)
    implements Serializable {

  public static EmailMessage from(CreateEmailRequest createEmailRequest, UUID id) {
    Instant now = Instant.now();
    return new EmailMessage(
        id.toString(),
        createEmailRequest.to(),
        Objects.requireNonNullElse(createEmailRequest.subject(), ""),
        Objects.requireNonNullElse(createEmailRequest.body(), ""),
        Duration.ofMillis(createEmailRequest.delayInMillis()),
        now,
        now.plusMillis(createEmailRequest.delayInMillis()));
  }
}
