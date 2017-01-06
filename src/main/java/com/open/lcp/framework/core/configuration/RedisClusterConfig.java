package com.open.lcp.framework.core.configuration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import com.open.dbs.cache.redis.RedisX;
import com.open.dbs.cache.redis.RedisXFactory;
import com.open.dbs.cache.redis.RedisXImpl;

@Configuration
public class RedisClusterConfig {

	//@Value("${expression.redis.pool.max-idle}")
	private int maxIdle;

	//@Value("${expression.redis.pool.min-idle}")
	private int minIdle;

	//@Value("${expression.redis.pool.max-active}")
	private int maxActive;

	//@Value("${expression.redis.pool.max-wait}")
	private long maxWait;

	private List<HostAndPort> redisHostList;

	//@Value("${mcp.redis.server}")
	public void setRedisHosts(String redisHosts) {
		redisHostList = new ArrayList<HostAndPort>();
		if (StringUtils.isEmpty(redisHosts)) {
			throw new IllegalArgumentException("redis host expected");
		}

		String[] hostAndPortList = redisHosts.split(",");
		for (String hostAndPort : hostAndPortList) {
			String[] arr = hostAndPort.split(":");
			if (arr.length != 2) {
				throw new IllegalArgumentException("illegal redis host: " + hostAndPort);
			}

			redisHostList.add(new HostAndPort(arr[0], Integer.parseInt(arr[1])));

		}
	}

	/*
	 * @Value("${expression.redis.pool.database}") private int database;
	 */

	@Bean
	public JedisPoolConfig jedisPoolConfig() {
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		jedisPoolConfig.setMaxIdle(maxIdle);
		jedisPoolConfig.setMinIdle(minIdle);
		jedisPoolConfig.setMaxWaitMillis(maxWait);
		jedisPoolConfig.setMaxTotal(maxActive);
		return jedisPoolConfig;
	}

	@Bean
	public JedisCluster jedisClusterTemplate() {
		Set<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();
		jedisClusterNodes.addAll(redisHostList);
		JedisCluster jc = new JedisCluster(jedisClusterNodes, 3000, 2000);// jedisPoolConfig()
		return jc;
	}

	@Bean(name = "redisCluster1")
	public RedisX getRedisCluster1() {
		return RedisXFactory.loadRedisX("redis_cluster_1", null);
	}
}
