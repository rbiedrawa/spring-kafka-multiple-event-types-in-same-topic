# Putting several event types in single kafka topic (PoC)

This repository contains sample Spring Boot setup for handling multiple event types in single kafka topic.

## Features

* Java Functions using Spring Cloud Stream Kafka Streams binder (`avro` project).
* Avro's unions with schema references.
* Avro Schema registration via gradle task.
* Protobuf schema references.
* Unit testing with `TopologyTestDriver`.

## Getting Started

### Prerequisite

* Java 11
* Docker
* Apache Kafka, Schema Registry

### Usage

* Start kafka platform.
  ```shell
  docker-compose -f docker/docker-compose.yml up -d
  ```

* List containers and check if all are `Up`.
    ```shell
    docker-compose -f docker/docker-compose.yml ps 

    #      Name                  Command            State                       Ports                     
    # ----------------------------------------------------------------------------------------------------
    # broker            /etc/confluent/docker/run   Up      0.0.0.0:9092->9092/tcp, 0.0.0.0:9101->9101/tcp
    # control-center    /etc/confluent/docker/run   Up      0.0.0.0:9021->9021/tcp                        
    # rest-proxy        /etc/confluent/docker/run   Up      0.0.0.0:8082->8082/tcp                        
    # schema-registry   /etc/confluent/docker/run   Up      0.0.0.0:8081->8081/tcp                        
    # zookeeper         /etc/confluent/docker/run   Up      0.0.0.0:2181->2181/tcp, 2888/tcp, 3888/tcp    
    ```

* Start one of the sample application:
  * **Avro**:
    * Register Schemas via gradle task:
      ```shell
      cd avro
      ./gradlew registerSchemasTask
      ```
    * Start application:
      ```shell
      ./gradlew bootRun
      ```
    * Check application logs to see that messages are correctly serialized and deserialized. Below sample output:
      ```shell
      # c.r.k.a.a.t.d.DummyTransactionGenerator  : Successfully sent TransactionEvent ca0e0357-e76e-4d8c-8015-e545111ea853 of type class com.rbiedrawa.app.avro.events.transactions.TransactionStarted
      # c.r.k.a.a.t.d.DummyTransactionGenerator  : Successfully sent TransactionEvent ca0e0357-e76e-4d8c-8015-e545111ea853 of type class com.rbiedrawa.app.avro.events.transactions.TransactionCompleted
      # c.r.k.a.a.t.TransactionKStream           : Consumed key ca0e0357-e76e-4d8c-8015-e545111ea853 value class com.rbiedrawa.app.avro.events.transactions.TransactionStarted
      # c.r.k.a.a.t.TransactionKStream           : Consumed key ca0e0357-e76e-4d8c-8015-e545111ea853 value class com.rbiedrawa.app.avro.events.transactions.TransactionCompleted
      ```
    
    * Use interactive query to get transaction from kafka state store:
      ```shell
      curl -X GET --location "http://localhost:8080/api/transactions/ca0e0357-e76e-4d8c-8015-e545111ea853"
      ```

  * **Protobuf**:
    * Start application:
      ```shell
      cd proto
      ./gradlew bootRun
      ```
    * Check application logs to see that messages are correctly serialized and deserialized. Below sample output:
      ```shell
      # c.r.k.a.t.d.DummyTransactionGenerator    : Successfully sent TransactionEvent d1e869d5-fb9b-4b13-ac43-678693d5910d of type TRANSACTION_STARTED
      # c.r.k.a.transactions.TransactionKStream  : Consumed transaction event of type TRANSACTION_STARTED. TransactionId d1e869d5-fb9b-4b13-ac43-678693d5910d
      # c.r.k.a.t.d.DummyTransactionGenerator    : Successfully sent TransactionEvent d1e869d5-fb9b-4b13-ac43-678693d5910d of type TRANSACTION_COMPLETED
      # c.r.k.a.transactions.TransactionKStream  : Consumed transaction event of type TRANSACTION_COMPLETED. TransactionId d1e869d5-fb9b-4b13-ac43-678693d5910d
      ```
    
    * Use interactive query to get transaction from kafka state store:
      ```shell
      curl -X GET --location "http://localhost:8080/api/transactions/d1e869d5-fb9b-4b13-ac43-678693d5910d"
      ```

* Stop docker-compose demo.
  ```shell
  docker-compose -f docker/docker-compose.yml down -v
  ```


## Important Endpoints

| Name | Endpoint | 
| -------------:|:--------:|
| `Spring Boot Application` | http://localhost:8080 |
| `Spring Boot Application - Actuator health` | http://localhost:8080/actuator/health |
| `Find transaction by id` | http://localhost:8080/api/transactions/{transactionId} |
| `Schema Registry` | http://localhost:8081 |
| `Schema Registry - Schemas` | http://localhost:8081/schemas |
| `Schema Registry - Find schema by id` | http://localhost:8081/schemas/ids/{id} |
| `Schema Registry - Subjects` | http://localhost:8081/subjects |
| `Confluent Control Center` | http://localhost:9021 |

## References

* [Should You Put Several Event Types in the Same Kafka Topic?](https://www.confluent.io/blog/put-several-event-types-kafka-topic/)

* [Putting Several Event Types in the Same Topic – Revisited](https://www.confluent.io/blog/multiple-event-types-in-the-same-kafka-topic/)

## License

Distributed under the MIT License. See `LICENSE` for more information.
