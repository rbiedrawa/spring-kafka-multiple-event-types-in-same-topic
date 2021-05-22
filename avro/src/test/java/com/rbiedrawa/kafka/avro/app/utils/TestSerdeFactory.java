package com.rbiedrawa.kafka.avro.app.utils;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.autoconfigure.kafka.KafkaProperties;

import com.rbiedrawa.kafka.avro.app.transactions.serdes.DefaultSerdeFactory;
import com.rbiedrawa.kafka.avro.app.transactions.serdes.SerdeFactory;

import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.avro.specific.SpecificRecord;

public class TestSerdeFactory implements SerdeFactory {

	private final DefaultSerdeFactory serdeFactory;

	public TestSerdeFactory() {
		this.serdeFactory = new DefaultSerdeFactory(newKafkaProperties());
	}

	@Override
	public <T extends SpecificRecord> SpecificAvroSerde<T> of(Class<T> clazz) {
		return serdeFactory.of(clazz);
	}

	@Override
	public <T extends SpecificRecord> SpecificAvroSerde<T> of(Class<T> clazz, boolean useRecordNameStrategy) {
		return serdeFactory.of(clazz, useRecordNameStrategy);
	}

	private KafkaProperties newKafkaProperties() {
		var kafkaProperties = new KafkaProperties();
		kafkaProperties.getProperties()
					   .putAll(defaultSerdeConfigs());
		return kafkaProperties;
	}

	private Map<String, String> defaultSerdeConfigs() {
		Map<String, String> configs = new HashMap<>();
		configs.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, "true");
		configs.put(KafkaAvroDeserializerConfig.USE_LATEST_VERSION, "true");
		configs.put(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, "mock://test-schema-registry");
		return configs;
	}
}
