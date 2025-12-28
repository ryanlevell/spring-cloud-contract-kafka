package com.example.demo1;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.kafka.KafkaContainer;

@TestConfiguration
public class KafkaTestConfig {

    @Bean
    @ServiceConnection // Spring Boot 3
    public KafkaContainer kafkaContainer() {
        return new KafkaContainer("apache/kafka:3.7.0");
    }
}