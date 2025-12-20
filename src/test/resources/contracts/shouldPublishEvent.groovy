package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Should publish event to Kafka topic"
    label "publish" // (!) must match method name that actually publishes with arg Map<String, Object>
    input {
        triggeredBy('publishSampleEvent()') // (!) must match method name that actually publishes
    }
    outputMessage {
        sentTo("events")
        body([
                '123': 'test-event'
        ])
    }
}
