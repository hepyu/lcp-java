package com.open.lcp.queue.kafka;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;

public class KafkaProducerHolder<K, V> {

	private ZKKafkaProducerConfig producerConfig;

	void setProducerConfig(ZKKafkaProducerConfig producerConfig) {
		this.producerConfig = producerConfig;
	}

	KafkaProducerHolder(ZKKafkaProducerConfig cfg) {
		initProducer();
		this.producerConfig = cfg;
	}

	private KafkaProducer<K, V> kafkaProducer;

	private void initProducer() {
		Properties props = new Properties();

		// props.put("bootstrap.servers",
		// "10.1.78.23:9091,10.1.78.23:9092,10.1.78.23:9093");
		// props.put("acks", "0");
		// props.put("retries", 0);
		// props.put("batch.size", 16384);
		// props.put("key.serializer",
		// "org.apache.kafka.common.serialization.StringSerializer");
		// props.put("value.serializer",
		// "org.apache.kafka.common.serialization.StringSerializer");

		props.put("bootstrap.servers", producerConfig.getBootstrapServers());
		props.put("acks", producerConfig.getAcks());
		props.put("retries", producerConfig.getRetries());
		props.put("batch.size", producerConfig.getBatchSize());
		props.put("key.serializer", producerConfig.getKeySerializer());
		props.put("value.serializer", producerConfig.getValueSerializer());
		kafkaProducer = new KafkaProducer<K, V>(props);
	}

	KafkaProducer<K, V> getKafkaProducer() {
		return kafkaProducer;
	}

}
