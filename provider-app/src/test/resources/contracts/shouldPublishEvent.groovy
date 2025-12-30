package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Should publish event to Kafka topic"
    label "publishSampleEvent" // called by consumer test
    input {
        triggeredBy('publishSampleEvent()') // implemented in provider base test
    }
    outputMessage {
        sentTo("events")
        body('Hello')
    }
}
