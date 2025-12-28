package com.example.demo1;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Import(KafkaTestConfig.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class ProviderAndConsumerIntegrationTest {

    @Autowired
    EventPublisher eventPublisher;

    @Autowired
    EventConsumer eventConsumer;

    @Test
    void shouldPublishAndConsumeEvent() {
        eventPublisher.publish("Hello!");
        await().untilAsserted(() -> assertTrue(!eventConsumer.events.isEmpty()));
    }
}
