package com.open.messagebus.kafka;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.log4j.Logger;

public class KafkaProducerXImpl<K, V> implements KafkaProducerX<K, V> {

	private static final Logger logger = Logger.getLogger(KafkaProducerXImpl.class);

	private final KafkaProducerHolder<K, V> kafkaProducerHolder;

	public KafkaProducerXImpl(ZKKafkaProducerConfig cfg) {
		this.kafkaProducerHolder = new KafkaProducerHolder<K, V>(cfg);
	}

	public KafkaProducerXImpl(KafkaProducerHolder<K, V> kafkaProducerHolder) {
		this.kafkaProducerHolder = kafkaProducerHolder;
	}

	private KafkaProducer<K, V> kafkaProducer() {
		return kafkaProducerHolder.getKafkaProducer();
	}

	@Override
	public void send(String topic, K key, V value) {
		send(topic, null, null, key, value);
	}

	@Override
	public void send(String topic, Long timestamp, K key, V value) {
		send(topic, null, timestamp, key, value);
	}

	@Override
	public void send(String topic, Integer partition, Long timestamp, K key, V value) {
		ProducerRecord<K, V> record = new ProducerRecord<K, V>(topic, partition, key, value);
		kafkaProducer().send(record, new Callback() {
			public void onCompletion(RecordMetadata metadata, Exception e) {
				if (e != null) {
					logger.warn(e.getMessage(), e);
				}
				logger.warn("message send to partition " + metadata.partition() + ", offset: " + metadata.offset());
			}
		});
	}

	KafkaProducerHolder<K, V> getKafkaProducerHolder() {
		return this.kafkaProducerHolder;
	}

}
