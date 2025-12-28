package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Should publish event to Kafka topic"
    label "publishSampleEvent"
    input {
        triggeredBy('publishSampleEvent()') // must match method in base class
    }
    outputMessage {
        sentTo("events")
        body('Hello')
    }
}
