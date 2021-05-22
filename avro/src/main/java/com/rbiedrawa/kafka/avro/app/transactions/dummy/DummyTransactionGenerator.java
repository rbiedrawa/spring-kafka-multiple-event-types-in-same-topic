package com.rbiedrawa.kafka.avro.app.transactions.dummy;


import java.util.UUID;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.rbiedrawa.app.avro.events.transactions.TransactionCompleted;
import com.rbiedrawa.app.avro.events.transactions.TransactionStarted;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecord;

@Slf4j
@Service
@RequiredArgsConstructor
public class DummyTransactionGenerator {
	private static final String TRANSACTION_EVENTS_OUTPUT_BINDING = "transaction_events_output";

	private final StreamBridge streamBridge;

	@Scheduled(fixedDelay = 5_000)
	public void produceDummyTransactionEvents() {
		var transactionId = UUID.randomUUID().toString();

		send(transactionId, newTransactionStartedEvent(transactionId));
		send(transactionId, newTransactionCompletedEvent(transactionId));
	}

	@SneakyThrows
	private void send(String transactionId, SpecificRecord event) {
		var message = MessageBuilder.withPayload(event)
									.setHeader(KafkaHeaders.MESSAGE_KEY, transactionId).build();

		streamBridge.send(TRANSACTION_EVENTS_OUTPUT_BINDING, message);
		log.info("Successfully sent TransactionEvent {} of type {}", transactionId, event.getClass());
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
