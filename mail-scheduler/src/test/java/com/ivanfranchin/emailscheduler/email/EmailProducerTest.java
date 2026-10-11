package com.ivanfranchin.emailscheduler.email;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ivanfranchin.emailscheduler.email.event.EmailMessage;
import jakarta.jms.JMSException;
import jakarta.jms.ObjectMessage;
import jakarta.jms.Session;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import org.apache.activemq.ScheduledMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Import(EmailProducer.class)
@TestPropertySource(properties = "activemq.queue.scheduled-emails=scheduled-emails.events")
class EmailProducerTest {

  @Autowired private EmailProducer producer;

  @MockitoBean private JmsTemplate jmsTemplate;

  @Test
  void send_shouldSendMessageWithCorrectQueueAndDelay() throws JMSException {
    String id = UUID.randomUUID().toString();
    EmailMessage emailMessage =
        new EmailMessage(
            id,
            "test@example.com",
            "Subject",
            "Body",
            Duration.ofMillis(5000),
            Instant.now(),
            Instant.now().plusMillis(5000));

    producer.send(emailMessage);

    ArgumentCaptor<MessageCreator> messageCreatorCaptor =
        ArgumentCaptor.forClass(MessageCreator.class);
    verify(jmsTemplate).send(eq("scheduled-emails.events"), messageCreatorCaptor.capture());

    MessageCreator capturedCreator = messageCreatorCaptor.getValue();
    Session session = mock(Session.class);
    ObjectMessage objectMessage = mock(ObjectMessage.class);
    when(session.createObjectMessage(emailMessage)).thenReturn(objectMessage);

    capturedCreator.createMessage(session);

    ArgumentCaptor<Long> delayCaptor = ArgumentCaptor.forClass(Long.class);
    verify(objectMessage)
        .setLongProperty(eq(ScheduledMessage.AMQ_SCHEDULED_DELAY), delayCaptor.capture());

    assertThat(delayCaptor.getValue()).isEqualTo(5000L);
  }
}
