package com.open.lcp.message.kafka;

import java.util.Properties;
import org.apache.kafka.clients.consumer.KafkaConsumer;

public class KafkaConsumerHolder<K, V> {

	private ZKKafkaConsumerConfig consumerConfig;

	void setConsumerConfig(ZKKafkaConsumerConfig consumerConfig) {
		this.consumerConfig = consumerConfig;
	}

	KafkaConsumerHolder(ZKKafkaConsumerConfig cfg) {
		initConsumer();
		this.consumerConfig = cfg;
	}

	private KafkaConsumer<K, V> kafkaConsumer;

	private void initConsumer() {
		Properties props = new Properties();

		// props.put("bootstrap.servers",
		// "10.1.78.23:9091,10.1.78.23:9092,10.1.78.23:9093");
		// props.put("group.id", "12");
		// props.put("enable.auto.commit", "true");
		// props.put("auto.commit.interval.ms", "1000");
		// props.put("session.timeout.ms", "30000");
		// props.put("key.deserializer",
		// "org.apache.kafka.common.serialization.StringDeserializer");
		// props.put("value.deserializer",
		// "org.apache.kafka.common.serialization.StringDeserializer");

		props.put("bootstrap.servers", consumerConfig.getBootstrapServers());
		props.put("group.id", consumerConfig.getGroupId());
		props.put("enable.auto.commit", consumerConfig.isEnableAutoCommit());
		props.put("auto.commit.interval.ms", consumerConfig.getAutoCommitIntervalMs());
		props.put("session.timeout.ms", consumerConfig.getSessionTimeoutMs());
		props.put("key.deserializer", consumerConfig.getKeySerializer());
		props.put("value.deserializer", consumerConfig.getValueSerializer());

		kafkaConsumer = new KafkaConsumer<K, V>(props);
	}

	KafkaConsumer<K, V> getKafkaConsumer() {
		return kafkaConsumer;
	}

}
