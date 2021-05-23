package com.rbiedrawa.kafka.app.transactions;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.rbiedrawa.app.proto.events.transactions.TransactionAggregate;
import com.rbiedrawa.app.proto.events.transactions.TransactionEvent;
import com.rbiedrawa.kafka.app.config.KafkaConfiguration.Topics;
import com.rbiedrawa.kafka.app.transactions.serdes.SerdeFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Aggregator;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Named;
import org.apache.kafka.streams.state.Stores;
import org.jetbrains.annotations.NotNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionKStream {
	public static final String STORE_NAME = "transaction-aggregate-store";

	private final SerdeFactory serdeFactory;

	@Bean
	public KTable<String, TransactionAggregate> transactionStream(StreamsBuilder sb) {
		return sb.stream(Topics.TRANSACTIONS, Consumed.with(Serdes.String(), serdeFactory.of(TransactionEvent.class)))
				 .peek((transactionId, event) -> log.info("Consumed transaction event of type {}. TransactionId {}", event.getPayloadCase(), transactionId))
				 .groupByKey()
				 .aggregate(() -> TransactionAggregate.newBuilder().build(),
							transactionAggregator(),
							Named.as("aggregate-transaction-events"),
							Materialized.<String, TransactionAggregate>
								as(Stores.persistentKeyValueStore(STORE_NAME))
										.withKeySerde(Serdes.String())
										.withValueSerde(serdeFactory.of(TransactionAggregate.class)));
	}

	@NotNull
	private Aggregator<String, TransactionEvent, TransactionAggregate> transactionAggregator() {
		// EventType matching logic here (For simplicity we'll use protobuf payloadCase)
		return (transactionId, event, aggregate) -> TransactionAggregate.newBuilder(aggregate)
																		.setTransactionId(transactionId)
																		.setState(event.getPayloadCase().name())
																		.build();
	}
}
