package com.example.demo1;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class EventListener {

    public final List<String> events = new ArrayList<>();

    @KafkaListener(topics = "events", groupId = "demo-group")
    public void listen(String event) {
        log.info("Received event: \"{}\"", event);
        events.add(event);
    }
}
