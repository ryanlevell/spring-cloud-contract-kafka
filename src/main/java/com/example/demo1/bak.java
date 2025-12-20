package com.example.demo1;

import org.springframework.stereotype.Component;

import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

@Component
public class bak {

    private final SubmissionPublisher<String> publisher = new SubmissionPublisher<>();

    public Flow.Publisher<String> getPublisher() {
        return publisher;
    }

    public void publish(String event) {
        publisher.submit(event);
    }

    public void close() {
        publisher.close();
    }
}
