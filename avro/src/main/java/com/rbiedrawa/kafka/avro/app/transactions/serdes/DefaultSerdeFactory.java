package com.rbiedrawa.kafka.avro.app.transactions.serdes;


import static io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig.VALUE_SUBJECT_NAME_STRATEGY;

import java.util.HashMap;

import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.stereotype.Component;

import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecord;

@Slf4j
@Component
@AllArgsConstructor
public class DefaultSerdeFactory implements SerdeFactory {
	private final KafkaProperties kafkaProperties;

	@Override
	public <T extends SpecificRecord> SpecificAvroSerde<T> of(Class<T> clazz) {
		return of(clazz, false);
	}

	@Override
	public <T extends SpecificRecord> SpecificAvroSerde<T> of(Class<T> clazz, boolean useRecordNameStrategy) {
		var serde = new SpecificAvroSerde<T>();

		var configs = new HashMap<>(kafkaProperties.getProperties());
		if (useRecordNameStrategy) {
			configs.put(VALUE_SUBJECT_NAME_STRATEGY, "io.confluent.kafka.serializers.subject.RecordNameStrategy");
		}

		serde.configure(configs, false);
		log.info("Created SpecificAvroSerde bean of type {}", clazz);
		return serde;
	}

}