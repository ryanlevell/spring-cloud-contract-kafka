package com.example.demo1;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.awaitility.Awaitility.await;
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
    void shouldPublishAndConsumeEvent() {
        eventPublisher.publish("Hello!");
        await().untilAsserted(() -> assertTrue(!eventListener.events.isEmpty()));
    }
}
