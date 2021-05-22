package com.rbiedrawa.kafka.app.transactions;

import static org.apache.kafka.streams.StoreQueryParameters.fromNameAndType;
import static org.apache.kafka.streams.state.QueryableStoreTypes.keyValueStore;

import java.util.Optional;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rbiedrawa.app.proto.events.transactions.TransactionAggregate;

import com.google.protobuf.util.JsonFormat;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transactions")
public class TransactionController {

	private final StreamsBuilderFactoryBean streamsBuilderFactoryBean;

	@GetMapping(value = "/{transactionId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getItem(@PathVariable final String transactionId) {
		ReadOnlyKeyValueStore<String, TransactionAggregate> store = streamsBuilderFactoryBean.getKafkaStreams()
																							 .store(fromNameAndType(TransactionKStream.STORE_NAME, keyValueStore()));
		return Optional.ofNullable(store.get(transactionId))
					   .map(this::toJson)
					   .map(ResponseEntity::ok)
					   .orElseGet(() -> ResponseEntity.notFound().build());
	}

	@SneakyThrows
	private String toJson(TransactionAggregate proto) {
		return JsonFormat.printer().print(proto);
	}

}
