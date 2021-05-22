package com.rbiedrawa.kafka.app.transactions.dummy;


import java.time.Instant;
import java.util.UUID;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.rbiedrawa.app.proto.events.transactions.TransactionCompleted;
import com.rbiedrawa.app.proto.events.transactions.TransactionEvent;
import com.rbiedrawa.app.proto.events.transactions.TransactionStarted;
import com.rbiedrawa.kafka.app.config.KafkaConfiguration.Topics;

import com.google.protobuf.util.Timestamps;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DummyTransactionGenerator {
	private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;

	@Scheduled(fixedDelay = 5_000)
	public void produceDummyTransactionEvents() {
		var transactionId = UUID.randomUUID().toString();

		send(transactionId, newTransactionStartedEvent(transactionId));
		send(transactionId, newTransactionCompletedEvent(transactionId));
	}

	@SneakyThrows
	private void send(String transactionId, TransactionEvent event) {
		kafkaTemplate.send(Topics.TRANSACTIONS, transactionId, event).get();
		log.info("Successfully sent TransactionEvent {} of type {}", transactionId, event.getPayloadCase());
	}

	private TransactionEvent newTransactionStartedEvent(String transactionId) {
		return TransactionEvent.newBuilder()
							   .setEventId(UUID.randomUUID().toString())
							   .setCreateDate(Timestamps.fromMillis(Instant.now().toEpochMilli()))
							   .setTransactionStarted(TransactionStarted.newBuilder().setTransactionId(transactionId))
							   .build();
	}

	private TransactionEvent newTransactionCompletedEvent(String transactionId) {
		return TransactionEvent.newBuilder()
							   .setEventId(UUID.randomUUID().toString())
							   .setCreateDate(Timestamps.fromMillis(Instant.now().toEpochMilli()))
							   .setTransactionCompleted(TransactionCompleted.newBuilder().setTransactionId(transactionId))
							   .build();
	}
}
