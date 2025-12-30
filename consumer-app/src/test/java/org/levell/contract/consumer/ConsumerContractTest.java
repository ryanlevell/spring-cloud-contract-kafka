package org.levell.contract.consumer;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.contract.stubrunner.StubTrigger;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.cloud.contract.verifier.converter.YamlContract;
import org.springframework.cloud.contract.verifier.messaging.MessageVerifierSender;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.testcontainers.shaded.org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Map;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@Import({KafkaTestConfig.class, ConsumerContractTest.MessagingConfig.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureStubRunner(
        ids = "org.levell.contract:provider-app:+:stubs",
        stubsMode = StubRunnerProperties.StubsMode.LOCAL
)
class ConsumerContractTest {

    @Autowired
    StubTrigger stubTrigger;

    @Autowired
    EventConsumer eventConsumer;

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    @Test
    void shouldConsumeEventFromProducerContract() {
        // must match contract label
        stubTrigger.trigger("publishSampleEvent");

        // use production consumer to verify end-to-end flow
        await().untilAsserted(() -> assertTrue(!eventConsumer.receivedEvents.isEmpty()));
        assertEquals("Hello", eventConsumer.receivedEvents.get(0));
    }

    // additional code required when using plain Kafka instead of spring cloud stream
    @TestConfiguration
    static class MessagingConfig {

        @Bean
        MessageVerifierSender<Message<?>> standaloneMessageVerifier(KafkaTemplate<String, String> kafkaTemplate) {
            return new MessageVerifierSender<>() {

                @Override
                public void send(Message<?> message, String destination, @Nullable YamlContract contract) {
                    log.info("Publishing test message \"{}\"", message.getPayload());
                    kafkaTemplate.send(message);
                }

                @Override
                public <T> void send(T payload, Map<String, Object> headers, String destination, @Nullable YamlContract contract) {
                    // hack to remove quotes from string literal as SCC converts the original string to JSON adding quotes.
                    // https://github.com/spring-cloud/spring-cloud-contract/blob/main/spring-cloud-contract-stub-runner/src/main/java/org/springframework/cloud/contract/stubrunner/StubRunnerExecutor.java#L263
                    send(MessageBuilder.createMessage(String.valueOf(payload).replace("\"", ""), new MessageHeaders(Map.of(KafkaHeaders.TOPIC, destination))), destination, contract);
                }
            };
        }
    }
}
