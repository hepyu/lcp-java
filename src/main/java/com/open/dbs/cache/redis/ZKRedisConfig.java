package com.open.dbs.cache.redis;

import java.util.List;

public class ZKRedisConfig {

	private List<RedisConfig> server;

	public List<RedisConfig> getServer() {
		return server;
	}

	public void setServer(List<RedisConfig> server) {
		this.server = server;
	}

}
