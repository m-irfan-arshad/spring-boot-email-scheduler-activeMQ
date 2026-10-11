package com.ivanfranchin.emailscheduler.email.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "scheduled_emails")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailEntity {

  @Id private UUID id;

  @Column(name = "to_email")
  private String toEmail;

  private String subject;

  private String body;

  private Long delayInMillis;

  @Enumerated(EnumType.STRING)
  private EmailStatus status;

  private Instant createdAt;

  @PrePersist
  protected void onCreate() {
    if (id == null) {
      id = UUID.randomUUID();
    }
    if (createdAt == null) {
      createdAt = Instant.now();
    }
    if (status == null) {
      status = EmailStatus.PENDING;
    }
  }
}
