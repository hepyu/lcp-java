package com.open.lcp.dbs.cache.redis;

public class ZKRedisConfig {
	private int timeout;

	private int maxRedirections;

	private String redisHostAndPorts;

	private String password;

	private boolean isCluster;

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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isCluster() {
		return isCluster;
	}

	public void setCluster(boolean isCluster) {
		this.isCluster = isCluster;
	}

}
