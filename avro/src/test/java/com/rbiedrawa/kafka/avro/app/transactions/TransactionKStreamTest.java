package com.rbiedrawa.kafka.avro.app.transactions;

import static org.assertj.core.api.Assertions.*;

import java.util.UUID;
import java.util.function.Consumer;

import com.rbiedrawa.app.avro.events.transactions.TransactionAggregate;
import com.rbiedrawa.app.avro.events.transactions.TransactionCompleted;
import com.rbiedrawa.app.avro.events.transactions.TransactionStarted;
import com.rbiedrawa.kafka.avro.app.config.KafkaConfiguration.Topics;
import com.rbiedrawa.kafka.avro.app.transactions.serdes.SerdeFactory;
import com.rbiedrawa.kafka.avro.app.utils.TestSerdeFactory;
import com.rbiedrawa.kafka.avro.app.utils.TopologyTestDriverFactory;

import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.state.KeyValueStore;
import org.junit.jupiter.api.Test;

class TransactionKStreamTest {
	private final SerdeFactory serdeFactory = new TestSerdeFactory();
	private final SpecificAvroSerde<SpecificRecord> specificAvroSerde = serdeFactory.of(SpecificRecord.class);
	private final Serde<String> keySerde = Serdes.String();

	@Test
	void should_handle_multiple_events_in_same_topic() {
		// Given
		try (var testDriver = TopologyTestDriverFactory.create(setupTransactionKStream())) {
			var eventInputTopic = testDriver.createInputTopic(Topics.TRANSACTIONS, keySerde.serializer(), specificAvroSerde.serializer());
			KeyValueStore<String, TransactionAggregate> aggregateStateStore = testDriver.getKeyValueStore(TransactionKStream.STORE_NAME);

			var transactionId = UUID.randomUUID().toString();
			var transactionStartedEvent = newTransactionStartedEvent(transactionId);
			var transactionCompletedEvent = newTransactionCompletedEvent(transactionId);

			// When / Then
			eventInputTopic.pipeInput(transactionId, transactionStartedEvent);
			assertThat(aggregateStateStore.get(transactionId).getState()).isEqualTo(transactionStartedEvent.getSchema().getFullName());

			eventInputTopic.pipeInput(transactionId, transactionCompletedEvent);
			assertThat(aggregateStateStore.get(transactionId).getState()).isEqualTo(transactionCompletedEvent.getSchema().getFullName());
		}
	}

	private Consumer<StreamsBuilder> setupTransactionKStream() {
		return streamsBuilder -> {
			var cut = new TransactionKStream(serdeFactory);
			var transactionEventsStream = streamsBuilder.stream(Topics.TRANSACTIONS, Consumed.with(keySerde, specificAvroSerde));

			cut.transactionAggregateFunction().accept(transactionEventsStream);
		};
	}

	private TransactionStarted newTransactionStartedEvent(String transactionId) {
		return TransactionStarted.newBuilder()
								 .setEventId(UUID.randomUUID().toString())
								 .setTransactionId(transactionId)
								 .build();
	}

	private TransactionCompleted newTransactionCompletedEvent(String transactionId) {
		return TransactionCompleted.newBuilder()
								   .setEventId(UUID.randomUUID().toString())
								   .setTransactionId(transactionId)
								   .build();
	}
}