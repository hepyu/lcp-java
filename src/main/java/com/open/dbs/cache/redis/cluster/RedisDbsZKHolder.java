package com.open.dbs.cache.redis.cluster;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

public class RedisDbsZKHolder {

	private JedisCluster jedisCluster;

	public RedisDbsZKHolder(JedisCluster jedisCluster, ZKRedisConfig redisConfig) {
		this.jedisCluster = jedisCluster;
		setJedisCluster(redisConfig);
	}

	public JedisCluster getJedisCluster() {
		return jedisCluster;
	}

	public void setJedisCluster(ZKRedisConfig redisConfig) {
		List<HostAndPort> redisHostList = new ArrayList<HostAndPort>();
		String[] hostAndPortList = redisConfig.getRedisHostAndPorts().split(",");
		for (String hostAndPort : hostAndPortList) {
			String[] arr = hostAndPort.split(":");
			if (arr.length != 2) {
				throw new IllegalArgumentException("illegal redis host: " + hostAndPort);
			}
			redisHostList.add(new HostAndPort(arr[0], Integer.parseInt(arr[1])));
		}
		Set<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();
		jedisClusterNodes.addAll(redisHostList);
		// soTimeout: 等待Response超时时间
		GenericObjectPoolConfig config = new GenericObjectPoolConfig();
		config.setMaxTotal(300);
		// jedisCluster = new JedisCluster(jedisClusterNodes,
		// redisConfig.getTimeout(), redisConfig.getTimeout(), 5,
		// "123456", config);

		jedisCluster = new JedisCluster(jedisClusterNodes, 180000, 180000, 5, "123456", config);
		// jedisCluster.set("test", "test");
		// jedisCluster.auth("123456");
	}

}
