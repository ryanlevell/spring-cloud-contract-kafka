package org.levell.contract.consumer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Import(KafkaTestConfig.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class ConsumerIntegrationTest {

    @Autowired
    EventConsumer eventConsumer;

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    @Test
    void shouldPublishAndConsumeEvent() throws InterruptedException {
        Thread.sleep(2000);
        kafkaTemplate.send("events", "Hello!");

        await().untilAsserted(() -> assertTrue(!eventConsumer.receivedEvents.isEmpty()));
        assertEquals("Hello!", eventConsumer.receivedEvents.get(0));
    }
}
