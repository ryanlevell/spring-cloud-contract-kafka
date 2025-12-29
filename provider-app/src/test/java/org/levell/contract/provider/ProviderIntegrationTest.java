package org.levell.contract.provider;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.ArrayList;
import java.util.List;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@Import(KafkaTestConfig.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ProviderIntegrationTest {

    @Autowired
    EventPublisher eventPublisher;

    List<String> events = new ArrayList<>();

    @Test
    void shouldPublishAndConsumeEvent() {
        eventPublisher.publish("Hello!");

        await().untilAsserted(() -> assertTrue(!events.isEmpty()));
        assertEquals("Hello!", events.get(0));
    }

    @KafkaListener(topics = "events", groupId = "integration-test-group")
    public void listen(String event) {
        log.info("Received message in integration test group.");
        events.add(event);
    }
}
