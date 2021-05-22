package com.rbiedrawa.kafka.avro.app.config;

import static com.rbiedrawa.kafka.avro.app.config.KafkaConfiguration.Topics.TRANSACTIONS;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;

@EnableKafka
@Configuration
public class KafkaConfiguration {
	private static final int DEFAULT_PARTITION_COUNT = 6;

	@Bean
	NewTopic transactions() {
		return TopicBuilder.name(TRANSACTIONS)
						   .partitions(DEFAULT_PARTITION_COUNT)
						   .build();
	}

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static final class Topics {
		public static final String TRANSACTIONS = "transaction.events";
	}
}
