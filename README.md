# Spring Cloud Contract Messaging

### Provider Side

1. Consists of:
    1. `EventPublisher` to publish events.
    2. `ContractBaseTest` as the base class for contract tests.
    3. `shouldPublishEvent.groovy` is the actual contract.
    4. `spring-cloud-contract-maven-plugin` to generate `ContractVerifierTest`.
2. Run Provider tests and install `stubs` locally:
    1. `mvn clean install`
    2. This:
        1. Runs the contract test and verifies the Provider implementation handles the contract successfully.
        2. Install the `stub` jar in the `.m2` folder.



