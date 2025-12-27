package com.example.demo1;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@Import(KafkaTestConfig.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class Demo1ApplicationTests {

    @Autowired
    EventPublisher eventPublisher;

    @Autowired
    EventListener eventListener;

    @Test
    @SneakyThrows
    void shouldPublishAndConsumeEvent() {
        eventPublisher.publish("Hello!");

        await().untilAsserted(() -> assertTrue(!eventListener.events.isEmpty()));
    }
}
