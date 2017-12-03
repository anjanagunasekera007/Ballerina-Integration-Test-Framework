# Ballerina Integration Test Framework - Emulator Server

The emulator server is responsible for starting all the backend servers required to run the test framework

## Installation

`mvn clean compile assembly:single`

## Running

`java -jar target/emulator-service-1.0.0-SNAPSHOT-jar-with-dependencies.jar <server name>`

Here replace `server name` with the server that is needed to test the Ballerina service.

## Servers Available

1) copyHeaders
2) malformedPayload
3) writingConnectionDrop
4) readingConnectionDrop
5) chunkingDisabled
6) slowWriter
7) httpVersion10
8) keepAlive
9) SlowResponse
10) largePayload
11) readingDelay
12) randomDrop
13) Emulator
14) MissingHeader
15) invalidSpec
16) slowWritingLargePayload
17) normal
