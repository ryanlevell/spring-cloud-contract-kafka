# Spring Cloud Contract Messaging

An example of Spring Cloud Contract with Messaging with Apache Kafka Docker container via Testcontainers.

This example consists of two parts:

1. Provider that publishes events to Kafka and the Provider contract test.
2. Consumer that listens for events from Kafka and the Consumer contract test.

[Spring Cloud Contract Documentation](https://docs.spring.io/spring-cloud-contract/reference/index.html)

### Provider Side

1. Add Maven dependency for Spring Cloud Contract.
2. Add Maven Spring Cloud Contract plugin to define base test class for generated tests.
3. Define a contract: `shouldPublishEvent.groovy`.
    1. The default location is `src/test/resources/contracts`.
4. Create a base test class `ContractBaseTest`
    1. Sets up the environment
    2. Implements the `triggeredBy` method to publish events to Kafka.
5. `cd provider-app && mvn clean install`
    1. Convert the Groovy contract to YAML at `target/generated-test-resources`.
    2. Package the contract as a distributable jar (`stubs` jar).
    3. Generate the provider test at `target/generated-test-sources` (extends `ContractBaseTest`).
    4. Run the generated provider test to verify the provider adheres to the contract.
    5. Publish the `stubs` jar to the local Maven `.m2` repository.

### Consumer Side

1. Add Maven dependency for Spring Cloud Contract.
2. Add Maven dependency for the `stubs` jar published by the provider.
3. `cd consumer-app && mvn clean test`
    1. Define a consumer contract test that uses `@AutoConfigureStubRunner` to fetch the `stubs` jar from the local
       Maven repository.
    2. Run the consumer contract test to verify the consumer adheres to the contract.
