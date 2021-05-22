package com.rbiedrawa.kafka.avro.app.transactions;

import java.util.Optional;

import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rbiedrawa.app.avro.events.transactions.TransactionAggregate;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transactions")
public class TransactionController {

	private final InteractiveQueryService queryService;

	@GetMapping(value = "/{transactionId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getItem(@PathVariable final String transactionId) {
		ReadOnlyKeyValueStore<String, TransactionAggregate> store = queryService.getQueryableStore(TransactionKStream.STORE_NAME, QueryableStoreTypes.keyValueStore());

		return Optional.ofNullable(store.get(transactionId))
					   .map(SpecificRecordBase::toString)
					   .map(ResponseEntity::ok)
					   .orElseGet(() -> ResponseEntity.notFound().build());
	}

}