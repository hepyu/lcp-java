package com.open.lcp.dbs.cache.redis.cluster;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.open.lcp.dbs.cache.redis.ZKRedisConfig;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

public class JedisClusterHolder {

	private JedisCluster jedisCluster;

	public JedisClusterHolder() {
	}

	public JedisCluster getJedisCluster() {
		return jedisCluster;
	}

	public void setJedis(ZKRedisConfig redisConfig) {
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

		if (StringUtils.isEmpty(redisConfig.getPassword())) {
			jedisCluster = new JedisCluster(jedisClusterNodes, 180000, 180000, 5, config);
		} else {
			jedisCluster = new JedisCluster(jedisClusterNodes, 180000, 180000, 5, redisConfig.getPassword(), config);
		}

		// jedisCluster.set("test", "test");
		// jedisCluster.auth("123456");
	}

}
