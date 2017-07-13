package com.open.lcp.message.kafka;

public class ZKKafkaProducerConfig {

	public static final String SE = "kafka.serializer.StringEncoder";

	// bootstrap.servers
	private String bootstrapServers;

	// acks
	private int acks;

	// retries
	private int retries;

	// batch.size
	private int batchSize;

	// key.serializer
	private String keySerializer;

	// value.serializer
	private String valueSerializer;

	public String getBootstrapServers() {
		return bootstrapServers;
	}

	public void setBootstrapServers(String bootstrapServers) {
		this.bootstrapServers = bootstrapServers;
	}

	public int getAcks() {
		return acks;
	}

	public void setAcks(int acks) {
		this.acks = acks;
	}

	public int getRetries() {
		return retries;
	}

	public void setRetries(int retries) {
		this.retries = retries;
	}

	public int getBatchSize() {
		return batchSize;
	}

	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}

	public String getKeySerializer() {
		return keySerializer;
	}

	public void setKeySerializer(String keySerializer) {
		this.keySerializer = keySerializer;
	}

	public String getValueSerializer() {
		return valueSerializer;
	}

	public void setValueSerializer(String valueSerializer) {
		this.valueSerializer = valueSerializer;
	}

}
