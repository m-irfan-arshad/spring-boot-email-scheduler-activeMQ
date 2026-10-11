package com.ivanfranchin.emailscheduler.email;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Import(EmailSender.class)
class EmailSenderTest {

  @Autowired private EmailSender emailSender;

  @MockitoBean private JavaMailSender mailSender;

  @Test
  void send_shouldSendEmailWithCorrectFields() {
    String to = "recipient@example.com";
    String subject = "Test Subject";
    String body = "Test Body";

    emailSender.send(to, subject, body);

    ArgumentCaptor<SimpleMailMessage> messageCaptor =
        ArgumentCaptor.forClass(SimpleMailMessage.class);
    verify(mailSender).send(messageCaptor.capture());

    SimpleMailMessage capturedMessage = messageCaptor.getValue();
    assertThat(capturedMessage.getTo()).contains(to);
    assertThat(capturedMessage.getSubject()).isEqualTo(subject);
    assertThat(capturedMessage.getText()).isEqualTo(body);
  }
}
