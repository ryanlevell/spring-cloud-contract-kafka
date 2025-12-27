package com.example.demo1;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EventListener {

    @KafkaListener(topics = "events", groupId = "demo-group")
    public void listen(String event) {
        log.info("Received event: \"{}\"", event);
    }
}
