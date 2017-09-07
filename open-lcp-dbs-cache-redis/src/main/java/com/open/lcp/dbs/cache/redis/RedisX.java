package com.open.lcp.dbs.cache.redis;

import com.open.lcp.dbs.cache.CacheX;

public interface RedisX extends CacheX {

	public void close();

	// public void resetRedis(ZKRedisConfig redisConfig);

	// public <K, V> long set(K key, V v, int seconds);
	//
	// public <K> long del(K key);
	//
	// public <K, V> V get(K key, Class<V> clazz);
	//
	// public <K, V> long set(K key, V value);
	//
	// public <K, V> long setRenewal(K key, V v, long step, int seconds);
	//
	// public <K, V> V getRenewal(K key, Class<V> clazz, long step, int
	// seconds);
	//
	// public <K> long expire(K key, int seconds);
	//
	// public <K, V> long zrem(K key, V member);

	// public boolean exists(String... keys);

	// public int zadd(String key, Map<String, Double> scoreMembers);
	//
	// public int zadd(String key, String member, Double scoreMember);
	//
	// public Double zscore(String key, String zkey);
	//
	//
	// public <T> String set(String key, T t);

	// public <T> String set(String key, T t, int seconds);

	// public String set(String key, String str);
	//
	// public <T> T get(String key, Class<T> clazz);
	//
	// public String get(String key);
	//
	// public <T> List<T> hmget(String key, Class<T> clazz, String... ids);
	//
	// public Map<String, String> hgetAll(String key);
	//
	// public boolean hexists(String key, String field);
	//
	// public <T> String hmset(String key, Map<String, T> objs);
	//
	// public Long hset(String key, String hkey, String value);
	//
	// public <T> Long hset(String key, String hkey, T t);
	//
	// public <T> T hget(String key, String hkey, Class<T> clazz);
	//
	// public Long hdel(String key, String hkey);
	//
	// public Long zcount(String key, Double min, Double max);
	//
	// public Set<String> zrevrange(String key, Long start, Long end);
	//
	// public Long zrevrank(String key, String member);
	//
	// public Set<String> zrevrangeByScore(String key, Double max, Double min,
	// int offset, int count);
	//
	// public Set<String> zrangeByScore(String key, Double max, Double min);
	//
	// public Set<String> zrangeByScore(String key, Double max, Double min, int
	// offset, int count);
	//
	// public Double zincrby(String key, String member, Double score);
	//
	// public Long zrank(String key, String member);
	//
	// public Long zcount(String key, String min, String max);
	//
	// public Long del(String key);
	//
	//
	// public long incr(String key);
	//
	// public long decr(String key);
	//
	// public long zcard(String key);
	//
	// public long setnx(String key, String value);
	//
	// public Set<Tuple> zrevrangeByScoreWithScores(String key, Double max,
	// Double min, int offset, int count);
	//
	// public Set<Tuple> zrangeByScoreWithScores(String key, Double max, Double
	// min, int offset, int count);
	//
	// public Set<Tuple> zrevrangeWithScores(String key, long start, long end);
	//
	// public Set<String> zrange(String key, int start, int end);
	//
	// public String setex(String key, int seconds, String value);
	//
	// public long sadd(String key, String... member);
	//
	// public long scard(String key);
	//
	// public boolean sismember(final String key, final String member);
	//
	// public long srem(String key, String... member);
	//
	// public Set<String> sinter(String... keys);
}
