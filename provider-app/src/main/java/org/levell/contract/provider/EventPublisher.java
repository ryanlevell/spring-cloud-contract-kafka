package org.levell.contract.provider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class EventPublisher {

    private static final String TOPIC = "events";

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void publish(String event) throws InterruptedException {
        log.info("Publishing event: \"{}\"", event);
        Thread.sleep(2000); // wait for consumer to connect
        kafkaTemplate.send(TOPIC, event);
    }
}
