package com.open.dbs.cache.redis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

public class RedisDbsZKHolder {

	private JedisCluster jedisCluster;

	public RedisDbsZKHolder(JedisCluster jedisCluster, RedisConfig redisConfig) {
		this.jedisCluster = jedisCluster;
		setJedisCluster(redisConfig);
	}

	public JedisCluster getJedisCluster() {
		return jedisCluster;
	}

	public void setJedisCluster(RedisConfig redisConfig) {
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
		jedisCluster = new JedisCluster(jedisClusterNodes, redisConfig.getTimeout(), redisConfig.getMaxRedirections());
	}

}
