package com.ivanfranchin.emailscheduler.email;

import com.ivanfranchin.emailscheduler.email.event.EmailMessage;
import com.ivanfranchin.emailscheduler.email.model.EmailEntity;
import com.ivanfranchin.emailscheduler.email.model.EmailStatus;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class EmailConsumer {

  private final EmailSender emailSender;
  private final EmailRepository emailRepository;

  @JmsListener(destination = "${activemq.queue.scheduled-emails}")
  public void onMessage(@Payload EmailMessage emailMessage) {
    log.info(
        "Received the email {} from ActiveMQ with lag of {} ms",
        emailMessage,
        Duration.between(emailMessage.expectedReturnTime(), Instant.now()).toMillis());

    UUID emailId = UUID.fromString(emailMessage.id());
    var entityOpt = emailRepository.findById(emailId);
    if (entityOpt.isEmpty()) {
      log.warn("Email with id {} not found in database", emailMessage.id());
      return;
    }

    EmailEntity entity = entityOpt.get();
    if (entity.getStatus() == EmailStatus.CANCELLED) {
      log.info("Email {} was cancelled, skipping send", emailMessage.id());
      return;
    }

    if (entity.getStatus() == EmailStatus.SENT) {
      log.info("Email {} already sent", emailMessage.id());
      return;
    }

    entity.setStatus(EmailStatus.SENT);
    emailRepository.save(entity);

    try {
      emailSender.send(emailMessage.to(), emailMessage.subject(), emailMessage.body());
    } catch (Exception e) {
      log.error("Failed to send email {}: {}", emailMessage.id(), e.getMessage());
      entity.setStatus(EmailStatus.FAILED);
      emailRepository.save(entity);
    }
  }
}
