package com.ivanfranchin.emailscheduler.email;

import com.ivanfranchin.emailscheduler.email.event.EmailMessage;
import jakarta.jms.ObjectMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ScheduledMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class EmailProducer {

  private final JmsTemplate jmsTemplate;

  @Value("${activemq.queue.scheduled-emails}")
  private String queue;

  @Async
  public void send(EmailMessage emailMessage) {
    log.info("Sending email {} to ActiveMQ", emailMessage);
    jmsTemplate.send(
        queue,
        messageCreator -> {
          ObjectMessage message = messageCreator.createObjectMessage(emailMessage);
          message.setLongProperty(
              ScheduledMessage.AMQ_SCHEDULED_DELAY, emailMessage.delay().toMillis());
          return message;
        });
  }
}
