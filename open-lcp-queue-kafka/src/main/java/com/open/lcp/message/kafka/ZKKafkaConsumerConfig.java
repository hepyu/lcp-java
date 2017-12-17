package com.open.lcp.message.kafka;

public class ZKKafkaConsumerConfig {

	// bootstrap.servers
	private String bootstrapServers;

	// group.id
	private String groupId;

	// enable.auto.commit
	private boolean enableAutoCommit;

	// auto.commit.interval.ms
	private long autoCommitIntervalMs;

	// session.timeout.ms
	private long sessionTimeoutMs;

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

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public boolean isEnableAutoCommit() {
		return enableAutoCommit;
	}

	public void setEnableAutoCommit(boolean enableAutoCommit) {
		this.enableAutoCommit = enableAutoCommit;
	}

	public long getAutoCommitIntervalMs() {
		return autoCommitIntervalMs;
	}

	public void setAutoCommitIntervalMs(long autoCommitIntervalMs) {
		this.autoCommitIntervalMs = autoCommitIntervalMs;
	}

	public long getSessionTimeoutMs() {
		return sessionTimeoutMs;
	}

	public void setSessionTimeoutMs(long sessionTimeoutMs) {
		this.sessionTimeoutMs = sessionTimeoutMs;
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