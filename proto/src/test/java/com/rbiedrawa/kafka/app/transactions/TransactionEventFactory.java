package com.rbiedrawa.kafka.app.transactions;

import java.time.Instant;
import java.util.UUID;

import com.rbiedrawa.app.proto.events.transactions.TransactionCompleted;
import com.rbiedrawa.app.proto.events.transactions.TransactionEvent;
import com.rbiedrawa.app.proto.events.transactions.TransactionStarted;

import com.google.protobuf.util.Timestamps;

public final class TransactionEventFactory {
	private TransactionEventFactory() {
	}

	public static TransactionEvent newTransactionCompletedEvent(String transactionId) {
		return TransactionEvent.newBuilder()
							   .setEventId(UUID.randomUUID().toString())
							   .setCreateDate(Timestamps.fromMillis(Instant.now().toEpochMilli()))
							   .setTransactionCompleted(TransactionCompleted.newBuilder().setTransactionId(transactionId))
							   .build();
	}

	public static TransactionEvent newTransactionStartedEvent(String transactionId) {
		return TransactionEvent.newBuilder()
							   .setEventId(UUID.randomUUID().toString())
							   .setCreateDate(Timestamps.fromMillis(Instant.now().toEpochMilli()))
							   .setTransactionStarted(TransactionStarted.newBuilder().setTransactionId(transactionId))
							   .build();
	}
}
