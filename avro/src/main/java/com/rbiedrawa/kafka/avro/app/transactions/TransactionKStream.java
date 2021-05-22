package com.rbiedrawa.kafka.avro.app.transactions;

import java.util.function.Consumer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.rbiedrawa.app.avro.events.transactions.TransactionAggregate;
import com.rbiedrawa.app.avro.events.transactions.TransactionCompleted;
import com.rbiedrawa.app.avro.events.transactions.TransactionStarted;
import com.rbiedrawa.kafka.avro.app.transactions.serdes.SerdeFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.kstream.Aggregator;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Named;
import org.apache.kafka.streams.state.Stores;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class TransactionKStream {
	public static final String STORE_NAME = "transaction-store";

	private final SerdeFactory serdeFactory;

	@Bean
	public Consumer<KStream<String, SpecificRecord>> transactionAggregateFunction() {
		return stream -> stream.peek((key, value) -> log.info("Consumed key {} value {}", key, value.getClass()))
							   .groupByKey()
							   .aggregate(() -> TransactionAggregate.newBuilder().build(),
										  aggregate(),
										  Named.as("aggregate-transaction-events"),
										  Materialized
											  .<String, TransactionAggregate>as(Stores.persistentKeyValueStore(STORE_NAME))
											  .withKeySerde(Serdes.String())
											  .withValueSerde(serdeFactory.of(TransactionAggregate.class, true))
										 );
	}

	private Aggregator<String, SpecificRecord, TransactionAggregate> aggregate() {
		return (transactionId, event, aggregate) -> {
			aggregate.setTransactionId(transactionId);

			if (event instanceof TransactionStarted) {
				aggregate.setState(TransactionStarted.getClassSchema().getFullName());
			} else if (event instanceof TransactionCompleted) {
				aggregate.setState(TransactionCompleted.getClassSchema().getFullName());
			} else {
				throw new IllegalStateException(String.format("Unsupported avro type: %s", event.getSchema().getFullName()));
			}
			return aggregate;
		};
	}
}
