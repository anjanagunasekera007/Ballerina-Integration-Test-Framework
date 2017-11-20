# Ballerina Integration Test Framework - Server Agent

Ballerina server agent is responsible for running the synapse server with required configuration changes. Currently
following operations are supported through the agent.
- starting the Ballerina server
- stopping the Ballerina server

## Installation 

```sh
mvn package
```

## Running

```sh
java -jar -Dtransports.netty.conf=conf/netty-transports.yml target/ServerAgent-1.0.0-SNAPSHOT.jar
```