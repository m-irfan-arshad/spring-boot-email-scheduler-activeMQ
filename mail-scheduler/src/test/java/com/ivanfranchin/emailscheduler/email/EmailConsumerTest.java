package com.ivanfranchin.emailscheduler.email;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ivanfranchin.emailscheduler.email.event.EmailMessage;
import com.ivanfranchin.emailscheduler.email.model.EmailEntity;
import com.ivanfranchin.emailscheduler.email.model.EmailStatus;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Import(EmailConsumer.class)
class EmailConsumerTest {

  @Autowired private EmailConsumer consumer;

  @MockitoBean private EmailSender emailSender;
  @MockitoBean private EmailRepository repository;

  @Test
  void onMessage_shouldSendEmailWithCorrectArguments() {
    UUID id = UUID.randomUUID();
    String idString = id.toString();
    EmailMessage emailMessage =
        new EmailMessage(
            idString,
            "test@example.com",
            "Subject",
            "Body",
            Duration.ofMillis(1000),
            Instant.now(),
            Instant.now().plusMillis(1000));

    EmailEntity entity =
        EmailEntity.builder()
            .id(id)
            .toEmail("test@example.com")
            .subject("Subject")
            .body("Body")
            .status(EmailStatus.PENDING)
            .build();

    when(repository.findById(id)).thenReturn(Optional.of(entity));

    consumer.onMessage(emailMessage);

    verify(emailSender).send("test@example.com", "Subject", "Body");
  }
}
