package com.open.dbs.cache.redis;

public class RedisConfig {
	private int timeout;

	private int maxRedirections;

	private String redisHostAndPorts;

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public int getMaxRedirections() {
		return maxRedirections;
	}

	public void setMaxRedirections(int maxRedirections) {
		this.maxRedirections = maxRedirections;
	}

	public String getRedisHostAndPorts() {
		return redisHostAndPorts;
	}

	public void setRedisHostAndPorts(String redisHostAndPorts) {
		this.redisHostAndPorts = redisHostAndPorts;
	}

}
