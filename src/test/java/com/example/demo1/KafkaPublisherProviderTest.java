package com.example.demo1;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.cloud.contract.verifier.converter.YamlContract;
import org.springframework.cloud.contract.verifier.messaging.MessageVerifierReceiver;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.verifier.messaging.internal.ContractVerifierMessaging;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;


/*
*
* Added ~/.docker-java.properties with api.version=1.44 due to new docker desktop latest api not supported by test containers
*
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Testcontainers
@AutoConfigureMessageVerifier
@ActiveProfiles("test")
public abstract class KafkaPublisherProviderTest {

    @Container
    @ServiceConnection
    static KafkaContainer kafka = new KafkaContainer("apache/kafka:3.7.0");

    @Autowired
    EventStreamPublisher publisher;

    public void publishSampleEvent() {
        this.publisher.publish(Map.of("123", "test-event"));
    }


    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }


    @EnableKafka
    @Configuration
    static class TestConfig {

        @Bean
        @Primary
        KafkaMessageVerifier messageVerifier() {
            return new KafkaMessageVerifier();
        }

    }


    static class KafkaMessageVerifier implements MessageVerifierReceiver<Message<?>> {

        Map<String, BlockingQueue<Message<?>>> broker = new ConcurrentHashMap<>();


        @Override
        public Message receive(String destination, long timeout, TimeUnit timeUnit, @Nullable YamlContract contract) {
            System.out.println("Checking for message in destination: [" + destination + "]");
            broker.putIfAbsent(destination, new ArrayBlockingQueue<>(1));
            BlockingQueue<Message<?>> messageQueue = broker.get(destination);
            Message<?> message;
            try {
                message = messageQueue.poll(timeout, timeUnit);
            }
            catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (message != null) {
                System.out.println("Removed a message from a topic [" + destination + "]");
            }
            return message;
        }


        @KafkaListener(topics = "events")
        public void listen(String payload, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
            System.out.println("Got a message from a topic [" + topic + "]");
            Map<String, Object> headers = new HashMap<>();
            //new JsonKafkaHeaderMapper().toHeaders(payload.headers(), headers);
            broker.putIfAbsent(topic, new ArrayBlockingQueue<>(1));
            BlockingQueue<Message<?>> messageQueue = broker.get(topic);
            messageQueue.add(MessageBuilder.createMessage(payload, new MessageHeaders(headers)));
        }

        @Override
        public Message receive(String destination, YamlContract contract) {
            return receive(destination, 15, TimeUnit.SECONDS, contract);
        }

    }
}