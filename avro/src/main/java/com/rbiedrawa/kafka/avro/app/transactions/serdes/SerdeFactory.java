package com.rbiedrawa.kafka.avro.app.transactions.serdes;

import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.avro.specific.SpecificRecord;

public interface SerdeFactory {
	<T extends SpecificRecord> SpecificAvroSerde<T> of(Class<T> clazz);

	<T extends SpecificRecord> SpecificAvroSerde<T> of(Class<T> clazz, boolean useRecordNameStrategy);
}
