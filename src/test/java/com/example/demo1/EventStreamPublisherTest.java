package com.example.demo1;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@EmbeddedKafka(partitions = 1, topics = {"events"})
@DirtiesContext
class EventStreamPublisherTest {

    @Autowired
    private EventStreamPublisher eventStreamPublisher;

    private static final List<String> receivedEvents = new ArrayList<>();
    private static CountDownLatch latch;

    @KafkaListener(topics = "events", groupId = "test-group")
    public void listen(String message) {
        try {
            Map<String, Object> eventMap = new ObjectMapper().readValue(message, Map.class);
            receivedEvents.add(eventMap.get("Event 1").toString());
            latch.countDown();
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize message", e);
        }
    }

    @Test
    void testEventStreamPublisher() throws Exception {
        receivedEvents.clear();
        latch = new CountDownLatch(1);

        eventStreamPublisher.publish(Map.of("Event 1", "data1"));

        assertTrue(latch.await(10, TimeUnit.SECONDS), "Did not receive all events in time");
        assertEquals(1, receivedEvents.size());
        assertEquals("data1", receivedEvents.get(0));
    }
}
