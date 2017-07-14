package com.open.lcp.dbs.cache.redis.single;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.open.lcp.dbs.cache.redis.ZKRedisConfig;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class JedisPoolHolder {

	private JedisPool jedisPool;

	public JedisPoolHolder() {
	}

	public Jedis getResource() {
		return jedisPool.getResource();
	}

	public void setJedis(ZKRedisConfig redisConfig) {
		String[] hostAndPort = redisConfig.getRedisHostAndPorts().split(":");
		if (hostAndPort.length != 2) {
			throw new IllegalArgumentException("illegal redis host: " + redisConfig.getRedisHostAndPorts());
		}
		String host = hostAndPort[0];
		int port = Integer.valueOf(hostAndPort[1]);

		GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
		poolConfig.setMaxTotal(300);

		if (StringUtils.isEmpty(redisConfig.getPassword())) {
			jedisPool = new JedisPool(poolConfig, host, port, 180000);
		} else {
			jedisPool = new JedisPool(poolConfig, host, port, 180000, redisConfig.getPassword());
		}

		// jedisCluster.set("test", "test");
		// jedisCluster.auth("123456");
	}

}
