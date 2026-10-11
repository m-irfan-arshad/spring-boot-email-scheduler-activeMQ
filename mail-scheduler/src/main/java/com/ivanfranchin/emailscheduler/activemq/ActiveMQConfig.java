package com.ivanfranchin.emailscheduler.activemq;

import jakarta.jms.ConnectionFactory;
import java.util.List;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;

@EnableJms
@Configuration
public class ActiveMQConfig {

  private static final List<String> TRUSTED_PACKAGES =
      List.of("java.time", "com.ivanfranchin.emailscheduler.email.event");

  @Bean
  ConnectionFactory connectionFactory(@Value("${spring.activemq.broker-url}") String brokerURL) {
    ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(brokerURL);
    activeMQConnectionFactory.setTrustedPackages(TRUSTED_PACKAGES);
    return activeMQConnectionFactory;
  }
}
