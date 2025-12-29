package org.levell.contract.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class EventConsumer {

    public final List<String> receivedEvents = new ArrayList<>();

    @KafkaListener(topics = "events", groupId = "demo-group")
    public void listen(String event) {
        log.info("Received event: \"{}\"", event);
        receivedEvents.add(event);
    }
}
