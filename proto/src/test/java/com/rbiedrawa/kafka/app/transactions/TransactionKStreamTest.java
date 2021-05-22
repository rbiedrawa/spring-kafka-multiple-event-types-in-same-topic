package com.rbiedrawa.kafka.app.transactions;

import static org.assertj.core.api.Assertions.*;

import java.util.UUID;

import com.rbiedrawa.app.proto.events.transactions.TransactionAggregate;
import com.rbiedrawa.app.proto.events.transactions.TransactionEvent;
import com.rbiedrawa.kafka.app.config.KafkaConfiguration;
import com.rbiedrawa.kafka.app.transactions.serdes.SerdeFactory;
import com.rbiedrawa.kafka.app.utils.TestSerdeFactory;
import com.rbiedrawa.kafka.app.utils.TopologyTestDriverFactory;

import io.confluent.kafka.streams.serdes.protobuf.KafkaProtobufSerde;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.state.KeyValueStore;
import org.junit.jupiter.api.Test;

class TransactionKStreamTest {
	private final SerdeFactory serdeFactory = new TestSerdeFactory();
	private final KafkaProtobufSerde<TransactionEvent> transactionEventSerde = serdeFactory.of(TransactionEvent.class);
	private final Serde<String> keySerde = Serdes.String();

	@Test
	void should_handle_multiple_events_in_same_topic() {
		// Given
		var cut = new TransactionKStream(serdeFactory);

		try (var testDriver = TopologyTestDriverFactory.create(cut::transactionStream)) {
			var eventInputTopic = testDriver.createInputTopic(KafkaConfiguration.Topics.TRANSACTIONS, keySerde.serializer(), transactionEventSerde.serializer());
			KeyValueStore<String, TransactionAggregate> aggregateStateStore = testDriver.getKeyValueStore(TransactionKStream.STORE_NAME);

			var transactionId = UUID.randomUUID().toString();
			var transactionStartedEvent = TransactionEventFactory.newTransactionStartedEvent(transactionId);
			var transactionCompletedEvent = TransactionEventFactory.newTransactionCompletedEvent(transactionId);

			// When / Then
			eventInputTopic.pipeInput(transactionId, transactionStartedEvent);
			assertThat(aggregateStateStore.get(transactionId).getState()).isEqualTo(transactionStartedEvent.getPayloadCase().name());

			eventInputTopic.pipeInput(transactionId, transactionCompletedEvent);
			assertThat(aggregateStateStore.get(transactionId).getState()).isEqualTo(transactionCompletedEvent.getPayloadCase().name());
		}
	}

}