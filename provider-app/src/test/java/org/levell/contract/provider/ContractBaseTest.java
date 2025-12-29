package org.levell.contract.provider;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.contract.verifier.converter.YamlContract;
import org.springframework.cloud.contract.verifier.messaging.MessageVerifierReceiver;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

@Slf4j
@Import({KafkaTestConfig.class, ContractBaseTest.MessagingConfig.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureMessageVerifier
public abstract class ContractBaseTest {

    @Autowired
    EventPublisher eventPublisher;

    // method in contract 'triggeredBy'
    public void publishSampleEvent() {
        eventPublisher.publish("Hello");
    }

    // additional code required when using plain Kafka instead of spring cloud stream
    @TestConfiguration
    static class MessagingConfig {

        @Bean
        public MessageVerifierReceiver<Message<?>> contractVerifierMessaging() {

            return new MessageVerifierReceiver<Message<?>>() {
                final List<String> events = new ArrayList<>();

                // the return value of this method must match the contract
                @Override
                public Message<String> receive(String destination, long timeout, TimeUnit timeUnit, @javax.annotation.Nullable YamlContract contract) {
                    log.info("Checking for message in destination: [{}]", destination);
                    await().until(() -> !events.isEmpty());
                    return MessageBuilder.createMessage(events.get(0), new MessageHeaders(Map.of()));
                }

                @Override
                public Message<String> receive(String destination, YamlContract contract) {
                    return receive(destination, 5, TimeUnit.SECONDS, contract);
                }

                // use unique groupId to avoid competing with other groups e.g. the real group
                @KafkaListener(topics = "events", groupId = "contract-test-group")
                public void listen(String payload) {
                    log.info("Received message in contract test group.");
                    events.add(payload);
                }
            };
        }
    }
}
