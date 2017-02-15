package com.open.messagebus.kafka;

public interface KafkaProducerX<K, V> {

	public void send(String topic, K key, V value);

	public void send(String topic, Long timestamp, K key, V value);

	public void send(String topic, Integer partition, Long timestamp, K key, V value);
}
