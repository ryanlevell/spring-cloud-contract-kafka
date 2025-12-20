package com.example.demo1;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class EventStreamPublisher {

    private static final String TOPIC = "events";

    private final KafkaTemplate<String, String> kafkaTemplate;

    public EventStreamPublisher(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /// must be Map<String, Object> as that's what SCC expects
    public void publish(Map<String, Object> event) {
        try {
            String json = new ObjectMapper().writeValueAsString(event);
            kafkaTemplate.send(TOPIC, json);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
