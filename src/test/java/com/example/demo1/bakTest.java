//package com.example.demo1;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.Flow;
//import java.util.concurrent.TimeUnit;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
//class bakTest {
//
//    @Autowired
//    private EventStreamPublisher eventStreamPublisher;
//
//    @Test
//    void testEventStreamPublisher() throws InterruptedException {
//        List<String> receivedEvents = new ArrayList<>();
//        CountDownLatch latch = new CountDownLatch(3);
//
//        Flow.Subscriber<String> subscriber = new Flow.Subscriber<>() {
//            private Flow.Subscription subscription;
//
//            @Override
//            public void onSubscribe(Flow.Subscription subscription) {
//                this.subscription = subscription;
//                subscription.request(Long.MAX_VALUE);
//            }
//
//            @Override
//            public void onNext(String item) {
//                receivedEvents.add(item);
//                latch.countDown();
//            }
//
//            @Override
//            public void onError(Throwable throwable) {
//                fail("Error occurred: " + throwable.getMessage());
//            }
//
//            @Override
//            public void onComplete() {
//            }
//        };
//
//        eventStreamPublisher.getPublisher().subscribe(subscriber);
//
//        eventStreamPublisher.publish("Event 1");
//        eventStreamPublisher.publish("Event 2");
//        eventStreamPublisher.publish("Event 3");
//
//        assertTrue(latch.await(5, TimeUnit.SECONDS), "Did not receive all events in time");
//        assertEquals(3, receivedEvents.size());
//        assertEquals("Event 1", receivedEvents.get(0));
//        assertEquals("Event 2", receivedEvents.get(1));
//        assertEquals("Event 3", receivedEvents.get(2));
//    }
//}
