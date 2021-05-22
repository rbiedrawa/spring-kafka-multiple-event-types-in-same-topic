package com.rbiedrawa.kafka.app;

import org.springframework.stereotype.Component;

@Component
public class TransactionKStream {


	//@Bean
	// public Function<KStream<String, String>, KTable<String, String>> transactionAggregateFunction() {
	// 	return stream -> stream.toTable(Named.as("transaction-aggregate"),
	// 									Materialized.<String, String, KeyValueStore<Bytes, byte[]>>as(
	// 										"transaction-aggregate-store")
	// 												.withKeySerde(Serdes.String())
	// 												.withValueSerde(Serdes.String()));
	// }

}
