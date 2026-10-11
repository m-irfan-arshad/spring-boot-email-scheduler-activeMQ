package com.ivanfranchin.emailscheduler.email;

import com.ivanfranchin.emailscheduler.email.dto.CreateEmailRequest;
import com.ivanfranchin.emailscheduler.email.dto.EmailResponse;
import com.ivanfranchin.emailscheduler.email.event.EmailMessage;
import com.ivanfranchin.emailscheduler.email.model.EmailEntity;
import com.ivanfranchin.emailscheduler.email.model.EmailStatus;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class EmailService {

  private final EmailRepository repository;
  private final EmailProducer producer;

  public EmailMessage createEmail(CreateEmailRequest request) {
    EmailStatus status = request.delayInMillis() == 0 ? EmailStatus.IMMEDIATE : EmailStatus.PENDING;

    EmailEntity entity =
        EmailEntity.builder()
            .toEmail(request.to())
            .subject(request.subject() != null ? request.subject() : "")
            .body(request.body() != null ? request.body() : "")
            .delayInMillis(request.delayInMillis())
            .status(status)
            .build();

    EmailEntity saved = repository.save(entity);

    EmailMessage emailMessage = EmailMessage.from(request, saved.getId());
    producer.send(emailMessage);

    return emailMessage;
  }

  public List<EmailResponse> getEmails() {
    return repository.findByStatus(EmailStatus.PENDING).stream()
        .map(EmailResponse::fromEntity)
        .toList();
  }

  public boolean cancelEmail(UUID id) {
    return repository
        .findByIdAndStatus(id, EmailStatus.PENDING)
        .map(
            entity -> {
              entity.setStatus(EmailStatus.CANCELLED);
              repository.save(entity);
              return true;
            })
        .orElse(false);
  }
}
