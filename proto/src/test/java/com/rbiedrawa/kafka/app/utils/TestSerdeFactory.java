package com.rbiedrawa.kafka.app.utils;

import java.util.Map;

import com.rbiedrawa.kafka.app.transactions.serdes.SerdeFactory;

import com.google.protobuf.Message;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.protobuf.KafkaProtobufDeserializerConfig;
import io.confluent.kafka.streams.serdes.protobuf.KafkaProtobufSerde;

public class TestSerdeFactory implements SerdeFactory {

	@Override
	public <T extends Message> KafkaProtobufSerde<T> of(Class<T> clazz) {
		var serde = new KafkaProtobufSerde<>(clazz);
		serde.configure(Map.of(
			AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "mock://test-schema-registry",
			KafkaProtobufDeserializerConfig.SPECIFIC_PROTOBUF_VALUE_TYPE, clazz.getName()
							  ),
						false);
		return serde;
	}
}
