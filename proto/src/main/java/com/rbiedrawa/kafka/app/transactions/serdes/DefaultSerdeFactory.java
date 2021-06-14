package com.rbiedrawa.kafka.app.transactions.serdes;


import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.stereotype.Component;

import com.google.protobuf.Message;
import io.confluent.kafka.streams.serdes.protobuf.KafkaProtobufSerde;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class DefaultSerdeFactory implements SerdeFactory {
	private final KafkaProperties kafkaProperties;

	@Override
	public <T extends Message> KafkaProtobufSerde<T> of(Class<T> clazz) {
		var serde = new KafkaProtobufSerde<>(clazz);
		serde.configure(kafkaProperties.getProperties(), false);
		log.info("Created KafkaProtobufSerde bean of type {}", clazz);
		return serde;
	}

}