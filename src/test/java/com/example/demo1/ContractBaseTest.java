package com.example.demo1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.verifier.converter.YamlContract;
import org.springframework.cloud.contract.verifier.messaging.MessageVerifierReceiver;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.support.MessageBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Import({KafkaTestConfig.class, ContractBaseTest.MessagingConfig.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureMessageVerifier
class ContractBaseTest {

    @Autowired
    EventPublisher eventPublisher;

    // method in contract 'triggeredBy'
    public void publishSampleEvent() {
        eventPublisher.publish("Hello");
    }

    // needed when using plain Kafka instead of not using spring cloud stream
    @Configuration
    static class MessagingConfig {

        @Bean
        public MessageVerifierReceiver<Message<?>> contractVerifierMessaging() {

            return new MessageVerifierReceiver<Message<?>>() {
                Map<String, BlockingQueue<Message<?>>> broker = new ConcurrentHashMap<>();


                @Override
                public Message receive(String destination, long timeout, TimeUnit timeUnit, @javax.annotation.Nullable YamlContract contract) {
                    System.out.println("Checking for message in destination: [" + destination + "]");
                    broker.putIfAbsent(destination, new ArrayBlockingQueue<>(1));
                    BlockingQueue<Message<?>> messageQueue = broker.get(destination);
                    Message<?> message;
                    try {
                        message = messageQueue.poll(timeout, timeUnit);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    if (message != null) {
                        System.out.println("Removed a message from a topic [" + destination + "]");
                    }
                    return message;
                }


                @KafkaListener(topics = "events", groupId = "test-group")
                public void listen(String payload, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
                    System.out.println("Got a message from a topic [" + topic + "]");
                    Map<String, Object> headers = new HashMap<>();
                    broker.putIfAbsent(topic, new ArrayBlockingQueue<>(1));
                    BlockingQueue<Message<?>> messageQueue = broker.get(topic);
                    messageQueue.add(MessageBuilder.createMessage(payload, new MessageHeaders(headers)));
                }

                @Override
                public Message receive(String destination, YamlContract contract) {
                    return receive(destination, 15, TimeUnit.SECONDS, contract);
                }

            };
        }

//        @Bean
//        public MessageVerifierSender<Message<?>> contractVerifierMessaging(
//                KafkaTemplate<String, String> kafkaTemplate) {
//            return new MessageVerifierSender<Message<?>>() {
//                @Override
//                public void send(Message<?> message, String destination, @Nullable YamlContract contract) {
//                    kafkaTemplate.send(destination, (String) message.getPayload());
//                }
//
//                @Override
//                public <T> void send(T payload, Map<String, Object> headers, String destination, @Nullable YamlContract contract) {
//                    kafkaTemplate.send(destination, (String) payload);
//                }
//            };
//        }
    }
}
