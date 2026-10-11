package com.ivanfranchin.emailscheduler.email;

import com.ivanfranchin.emailscheduler.email.dto.CreateEmailRequest;
import com.ivanfranchin.emailscheduler.email.dto.EmailResponse;
import com.ivanfranchin.emailscheduler.email.event.EmailMessage;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/scheduled-emails")
public class EmailController {

  private final EmailService emailService;

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public EmailMessage createEmail(@Valid @RequestBody CreateEmailRequest request) {
    return emailService.createEmail(request);
  }

  @GetMapping
  public List<EmailResponse> getEmails() {
    return emailService.getEmails();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> cancelEmail(@PathVariable UUID id) {
    boolean cancelled = emailService.cancelEmail(id);
    return cancelled
        ? ResponseEntity.ok().build()
        : ResponseEntity.status(HttpStatus.CONFLICT).build();
  }
}
