package com.example.demo1;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class EventPublisher {

    private static final String TOPIC = "events";

    private final KafkaTemplate<String, String> kafkaTemplate;

    @SneakyThrows
    public void publish(String event) {
        log.info("Publishing event: \"{}\"", event);
        Thread.sleep(2000);
        kafkaTemplate.send(TOPIC, event);
    }
}
