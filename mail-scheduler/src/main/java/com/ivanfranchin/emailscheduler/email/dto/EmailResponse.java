package com.ivanfranchin.emailscheduler.email.dto;

import com.ivanfranchin.emailscheduler.email.model.EmailEntity;
import java.time.Instant;
import java.util.UUID;

public record EmailResponse(
    UUID id,
    String to,
    String subject,
    String body,
    Long delayInMillis,
    String status,
    Instant createdAt) {

  public static EmailResponse fromEntity(EmailEntity entity) {
    return new EmailResponse(
        entity.getId(),
        entity.getToEmail(),
        entity.getSubject(),
        entity.getBody(),
        entity.getDelayInMillis(),
        entity.getStatus().name(),
        entity.getCreatedAt());
  }
}
