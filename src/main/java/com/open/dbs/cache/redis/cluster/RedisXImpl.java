package com.open.dbs.cache.redis.cluster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.Tuple;

import com.google.gson.Gson;

public class RedisXImpl implements RedisX {

	private static Logger logger = Logger.getLogger(RedisXImpl.class);

	static Gson gson = new Gson();

	private JedisCluster jedisCluster;
	private RedisDbsZKHolder holder;

	public RedisXImpl(RedisConfig redisConfig) {
		holder = new RedisDbsZKHolder(jedisCluster, redisConfig);
	}

	private JedisCluster getJedisCluster() {
		return holder.getJedisCluster();
	}

	@Override
	public RedisDbsZKHolder getHolder() {
		return holder;
	}

	@Override
	public boolean exists(String... keys) {
		for (String key : keys) {
			if (!getJedisCluster().exists(key)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int zadd(String key, Map<String, Double> scoreMembers) {
		int ct = 0;
		if (StringUtils.isNotEmpty(key) && null != scoreMembers && scoreMembers.size() > 0) {
			long index = getJedisCluster().zadd(key, scoreMembers);
			if (index > 0)
				ct++;
		}

		return ct;
	}

	@Override
	public int zadd(String key, String member, Double scoreMember) {
		int ct = 0;
		if (StringUtils.isNotEmpty(key) && null != scoreMember && StringUtils.isNotEmpty(member)) {
			long index = getJedisCluster().zadd(key, scoreMember, member);
			if (index > 0)
				ct++;
		}

		return ct;
	}

	@Override
	public Double zscore(String key, String zkey) {
		if (StringUtils.isNotEmpty(key) && StringUtils.isNotEmpty(zkey)) {
			return getJedisCluster().zscore(key, zkey);
		}
		return null;
	}

	@Override
	public Long zrem(String key, String member) {
		if (StringUtils.isNotEmpty(key) && StringUtils.isNotEmpty(member)) {
			return getJedisCluster().zrem(key, member);
		}

		return null;
	}

	@Override
	public <T> String set(String key, T t) {
		try {
			if (StringUtils.isNotEmpty(key) && null != t)
				return getJedisCluster().set(key, gson.toJson(t));
		} catch (Exception e) {
			logger.error(e);
		}

		return null;
	}

	@Override
	public String set(String key, String str) {
		if (StringUtils.isNotEmpty(key) && null != str)
			return getJedisCluster().set(key, str);
		return null;
	}

	@Override
	public <T> T get(String key, Class<T> clazz) {
		String str = null;
		if (StringUtils.isNotEmpty(key) && null != clazz)
			str = getJedisCluster().get(key);
		try {
			if (StringUtils.isNotEmpty(str))
				return gson.fromJson(str, clazz);
		} catch (Exception e) {
			logger.error(e);
		}

		return null;
	}

	@Override
	public String get(String key) {
		if (StringUtils.isNotEmpty(key))
			return getJedisCluster().get(key);
		return null;
	}

	@Override
	public <T> List<T> hmget(String key, Class<T> clazz, String... ids) {
		List<T> results = null;
		if (StringUtils.isNotEmpty(key) && null != clazz && null != ids && ids.length > 0) {
			List<String> vals = getJedisCluster().hmget(key, ids);
			if (null != vals && vals.size() > 0) {
				for (String val : vals) {
					try {
						if (StringUtils.isNotEmpty(val)) {
							// T t = objMapper.readValue(val, clazz);
							T t = gson.fromJson(val, clazz);
							if (null != t) {
								if (null == results)
									results = new ArrayList<T>();
								results.add(t);
							}
						}
					} catch (Exception e) {
						logger.error(e);
					}
				}
			}
		}

		return results;
	}

	@Override
	public Map<String, String> hgetAll(String key) {
		if (StringUtils.isNotEmpty(key)) {
			return getJedisCluster().hgetAll(key);
		}
		return null;
	}

	@Override
	public boolean hexists(String key, String field) {
		if (StringUtils.isNotEmpty(key)) {
			return getJedisCluster().hexists(key, field);
		}
		return false;
	}

	@Override
	public <T> String hmset(String key, Map<String, T> objs) {
		if (null != key && null != objs && objs.size() > 0) {
			Map<String, String> map = new HashMap<String, String>();
			for (String k : objs.keySet()) {
				T t = objs.get(k);
				try {
					map.put(k, gson.toJson(t));
					// map.put(k, objMapper.writeValueAsString(t));
				} catch (Exception e) {
					logger.error(e);
				}
			}
			if (null != map && map.size() > 0)
				return getJedisCluster().hmset(key, map);
		}

		return null;
	}

	@Override
	public Long hset(String key, String hkey, String value) {
		if (StringUtils.isNotEmpty(key)) {
			return getJedisCluster().hset(key, hkey, value);
		}
		return null;
	}

	@Override
	public <T> Long hset(String key, String hkey, T t) {
		if (StringUtils.isNotEmpty(key) && null != t) {
			String value = null;
			try {
				value = gson.toJson(t);// objMapper.writeValueAsString(t);
				return getJedisCluster().hset(key, hkey, value);
			} catch (Exception e) {
				logger.error(e);
			}
		}

		return null;
	}

	@Override
	public <T> T hget(String key, String hkey, Class<T> clazz) {

		String str = null;
		if (StringUtils.isNotEmpty(key) && null != clazz)
			str = getJedisCluster().hget(key, hkey);
		try {
			if (StringUtils.isNotEmpty(str))
				return gson.fromJson(str, clazz);// objMapper.readValue(str,
													// clazz);
		} catch (Exception e) {
			logger.error(e);
		}

		return null;
	}

	@Override
	public Long hdel(String key, String hkey) {
		if (StringUtils.isNotEmpty(key) && StringUtils.isNotEmpty(hkey))
			return getJedisCluster().hdel(key, hkey);

		return null;
	}

	@Override
	public Long zcount(String key, Double min, Double max) {
		if (StringUtils.isNotEmpty(key) && null != min && null != max)
			return getJedisCluster().zcount(key, min, max);

		return null;
	}

	@Override
	public Set<String> zrevrange(String key, Long start, Long end) {
		if (StringUtils.isNotEmpty(key) && null != start && null != end)
			return getJedisCluster().zrevrange(key, start, end);
		return null;
	}

	@Override
	public Long zrevrank(String key, String member) {

		if (StringUtils.isNotEmpty(key) && StringUtils.isNotEmpty(member)) {
			return getJedisCluster().zrevrank(key, member);
		}
		return null;
	}

	@Override
	public Set<String> zrevrangeByScore(String key, Double max, Double min, int offset, int count) {
		if (StringUtils.isNotEmpty(key) && null != min && null != max)
			return getJedisCluster().zrevrangeByScore(key, max, min, offset, count);
		return null;
	}

	@Override
	public Set<String> zrangeByScore(String key, Double max, Double min) {
		if (StringUtils.isNotEmpty(key) && null != min && null != max)
			return getJedisCluster().zrangeByScore(key, max, min);
		return null;
	}

	@Override
	public Set<String> zrangeByScore(String key, Double max, Double min, int offset, int count) {
		return getJedisCluster().zrangeByScore(key, min, max, offset, count);
	}

	@Override
	public Double zincrby(String key, String member, Double score) {
		if (StringUtils.isNotEmpty(key) && StringUtils.isNotEmpty(member) && null != score)
			return getJedisCluster().zincrby(key, score, member);
		return null;
	}

	@Override
	public Long zrank(String key, String member) {
		if (StringUtils.isNotEmpty(key) && StringUtils.isNotEmpty(member))
			return getJedisCluster().zrank(key, member);
		return null;
	}

	@Override
	public Long zcount(String key, String min, String max) {
		if (StringUtils.isNotEmpty(key))
			return getJedisCluster().zcount(key, min, max);
		return null;
	}

	@Override
	public Long del(String key) {
		if (StringUtils.isNotEmpty(key))
			return getJedisCluster().del(key);
		return null;
	}

	@Override
	public void expire(String key, int seconds) {
		if (StringUtils.isNotEmpty(key))
			getJedisCluster().expire(key, seconds);
	}

	@Override
	public long incr(String key) {
		return getJedisCluster().incr(key);
	}

	@Override
	public long decr(String key) {
		return getJedisCluster().decr(key);
	}

	@Override
	public long zcard(String key) {
		return getJedisCluster().zcard(key);
	}

	@Override
	public long setnx(String key, String value) {
		return getJedisCluster().setnx(key, value);
	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(String key, Double max, Double min, int offset, int count) {
		return getJedisCluster().zrevrangeByScoreWithScores(key, max, min, offset, count);
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(String key, Double max, Double min, int offset, int count) {
		return getJedisCluster().zrangeByScoreWithScores(key, min, max, offset, count);
	}

	@Override
	public Set<Tuple> zrevrangeWithScores(String key, long start, long end) {
		return getJedisCluster().zrevrangeWithScores(key, start, end);
	}

	@Override
	public Set<String> zrange(String key, int start, int end) {
		return getJedisCluster().zrange(key, start, end);
	}

	@Override
	public String setex(String key, int seconds, String value) {
		return getJedisCluster().setex(key, seconds, value);
	}

	@Override
	public long sadd(String key, String... member) {
		if (StringUtils.isEmpty(key))
			return 0;

		if (ArrayUtils.isEmpty(member))
			return 0;

		return getJedisCluster().sadd(key, member);
	}

	@Override
	public long scard(String key) {
		if (StringUtils.isEmpty(key))
			return 0;

		return getJedisCluster().scard(key);
	}

	@Override
	public boolean sismember(final String key, final String member) {
		return getJedisCluster().sismember(key, member);
	}

	@Override
	public long srem(String key, String... member) {
		return getJedisCluster().srem(key, member);
	}

	@Override
	public Set<String> sinter(String... keys) {
		return getJedisCluster().sinter(keys);

	}

}