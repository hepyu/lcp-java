package com.open.dbs.cache.redis.cluster;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.open.common.JsonObjectConv;
import com.open.dbs.cache.Renewal;
import com.open.dbs.cache.redis.RedisX;
import com.open.dbs.cache.redis.ZKRedisConfig;

import redis.clients.jedis.GeoRadiusResponse;
import redis.clients.jedis.GeoUnit;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.params.geo.GeoRadiusParam;

public class JedisClusterImpl implements RedisX {

	private static Logger logger = Logger.getLogger(JedisClusterImpl.class);

	public static final JsonObjectConv jsonConv = new JsonObjectConv();

	static Gson gson = new Gson();

	private JedisClusterHolder holder;

	public JedisClusterImpl(ZKRedisConfig redisConfig) {
		holder = new JedisClusterHolder();
		holder.setJedis(redisConfig);
	}

	@Override
	public void close() {
		try {
			getJedisCluster().close();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	// @Override
	// public void resetRedis(ZKRedisConfig redisConfig) {
	// holder.setJedis(redisConfig);
	//
	// }

	private JedisCluster getJedisCluster() {
		return holder.getJedisCluster();
	}

	@Override
	public <K, MEMBER> long geoAdd(K key, double longitude, double latitude, MEMBER member) {
		return getJedisCluster().geoadd(jsonConv.bytes(key), longitude, latitude, jsonConv.bytes(member));
	}

	@Override
	public <K> List<GeoRadiusResponse> geoRadius(K key, double longitude, double latitude, double radius, GeoUnit unit,
			GeoRadiusParam param) {
		return getJedisCluster().georadius(jsonConv.bytes(key), longitude, latitude, radius, unit, param);
	}

	@Override
	public <K, V> long set(K key, V v, int seconds) {
		if (key != null) {
			byte[] keybytes = jsonConv.bytes(key);
			getJedisCluster().set(keybytes, jsonConv.bytes(v));
			getJedisCluster().expire(keybytes, seconds);
			return 1;
		} else {
			logger.warn("key is null.");
			return 0;
		}
	}

	@Override
	public <K, V> long set(K key, V value) {
		if (key != null) {
			byte[] keybytes = jsonConv.bytes(key);
			getJedisCluster().set(keybytes, jsonConv.bytes(value));
			return 0;
		} else {
			return -1;
		}
	}

	@Override
	public <K> long del(K key) {
		if (key != null) {
			return getJedisCluster().del(jsonConv.bytes(key));
		} else {
			return 0;
		}
	}

	@Override
	public <K, V> V get(K key, Class<V> clazz) {
		return jsonConv.toObject(getJedisCluster().get(jsonConv.bytes(key)), clazz);
	}

	@Override
	public <K, V> long setRenewal(K key, V v, long step, int seconds) {
		if (key != null) {
			byte[] keybytes = jsonConv.bytes(key);
			getJedisCluster().set(keybytes,
					jsonConv.bytes(new Renewal<V>(System.currentTimeMillis() + seconds * 1000, v)));
			getJedisCluster().expire(keybytes, seconds);
			return 1;
		} else {
			return 0;
		}
	}

	@Override
	public <K, V> V getRenewal(K key, Class<V> clazz, long step, int seconds) {
		if (step > seconds) {
			step = seconds;
		}
		@SuppressWarnings("unchecked")
		Renewal<V> renewal = this.get(key, Renewal.class);
		if (renewal == null) {
			return null;
		}
		if (renewal.getTick() < System.currentTimeMillis() + step * 1000) {
			renewal.setTick(System.currentTimeMillis() + seconds * 1000);
			byte[] keybytes = jsonConv.bytes(key);
			getJedisCluster().set(keybytes, jsonConv.bytes(renewal));
			getJedisCluster().expire(keybytes, seconds);
		}
		String json = JsonObjectConv.gson.toJson(renewal.getV());
		return JsonObjectConv.gson.fromJson(json, clazz);
	}

	@Override
	public <K, V> long setx(K key, V v, int seconds) {
		byte[] keybytes = jsonConv.bytes(key);
		getJedisCluster().set(keybytes, jsonConv.bytes(v));
		getJedisCluster().expire(keybytes, seconds);
		return 1;
	}

	@Override
	public <K, V> long setnx(K key, V v) {
		return getJedisCluster().setnx(jsonConv.bytes(key), jsonConv.bytes(v));
	}

	@Override
	public <K, V> Map<K, V> mget(K[] keys, Class<K> clazzK, Class<V> clazzV) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <K> long incr(K key, long value) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <K> long expired(K key, int seconds) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <K> int seconds(K key) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <K> long exists(K key) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <K, V> V getset(K key, V v, Class<V> clazz) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <K> long multi_del(K[] keys) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <K, HK, V> long hset(K key, HK hkey, V value) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <K, HK, V> V hget(K key, HK hkey, Class<V> clazz) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <K, HK, V> Map<HK, V> hmget(K key, HK[] hkeys, Class<HK> clazzHK, Class<V> clazzV) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <K, HK, V> Map<HK, V> hgetall(K key, Class<HK> clazzHK, Class<V> clazzV) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <K, HK> long hincr(K key, HK hkey, long value) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <K, HK, V> long multi_hset(K key, Map<HK, V> values) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <K, HK> long multi_hdel(K key, HK[] hkeys) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <K, HK> long hdel(K key, HK hkey) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <K> long hsize(K key) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <K> long hclear(K key) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <K, HK> long hexists(K key, HK hkey) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <K> long zclear(K key) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <K> long zcount(K key, String score_start, String score_end) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <K, V> long zget(K key, V value) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <K, V> long zset(K key, V value, long score) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <K, V> long zdel(K key, V value) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <K, V> Map<V, Long> zrange(K key, int offset, int limit, Class<V> clazz) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <K, V> Map<V, Long> zrrange(K key, int offset, int limit, Class<V> clazz) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <K, V> long zrank(K key, V value) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <K, V> long zrrank(K key, V value) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <K, V> long zincr(K key, V value, long incr) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <K> long zsize(K key) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <K, V> long zdecr(K key, V value, long incr) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <K, V> long multi_zset(K key, Map<V, Long> values) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <K, V> Map<V, Long> multi_zget(K key, V[] values, Class<V> clazzV) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <K> long zremrangebyrank(K key, int start, int end) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <K, V> Map<V, Long> zscan(K key, V value_start, String score_start, String score_end, long limit,
			Class<V> clazz) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <K, V> Map<V, Long> zrscan(K key, V value_start, String score_start, String score_end, long limit,
			Class<V> clazz) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <K, V> long zexists(K key, V value) {
		// TODO Auto-generated method stub
		return 0;
	}

	// @Override
	// public String get(String key) {
	// if (StringUtils.isNotEmpty(key))
	// return getJedisCluster().get(key);
	// return null;
	// }

	// @Override
	// public boolean exists(String... keys) {
	// for (String key : keys) {
	// if (!getJedisCluster().exists(key)) {
	// return false;
	// }
	// }
	// return true;
	// }
	//
	// @Override
	// public int zadd(String key, Map<String, Double> scoreMembers) {
	// int ct = 0;
	// if (StringUtils.isNotEmpty(key) && null != scoreMembers &&
	// scoreMembers.size() > 0) {
	// long index = getJedisCluster().zadd(key, scoreMembers);
	// if (index > 0)
	// ct++;
	// }
	//
	// return ct;
	// }
	//
	// @Override
	// public int zadd(String key, String member, Double scoreMember) {
	// int ct = 0;
	// if (StringUtils.isNotEmpty(key) && null != scoreMember &&
	// StringUtils.isNotEmpty(member)) {
	// long index = getJedisCluster().zadd(key, scoreMember, member);
	// if (index > 0)
	// ct++;
	// }
	//
	// return ct;
	// }
	//
	// @Override
	// public Double zscore(String key, String zkey) {
	// if (StringUtils.isNotEmpty(key) && StringUtils.isNotEmpty(zkey)) {
	// return getJedisCluster().zscore(key, zkey);
	// }
	// return null;
	// }
	//
	// @Override
	// public Long zrem(String key, String member) {
	// if (StringUtils.isNotEmpty(key) && StringUtils.isNotEmpty(member)) {
	// return getJedisCluster().zrem(key, member);
	// }
	//
	// return null;
	// }
	//
	// @Override
	// public <T> String set(String key, T t) {
	// try {
	// if (StringUtils.isNotEmpty(key) && null != t)
	// return getJedisCluster().set(key, gson.toJson(t));
	// } catch (Exception e) {
	// logger.error(e);
	// }
	//
	// return null;
	// }
	//
	// @Override
	// public String set(String key, String str) {
	// if (StringUtils.isNotEmpty(key) && null != str)
	// return getJedisCluster().set(key, str);
	// return null;
	// }
	//
	// @Override
	// public <T> T get(String key, Class<T> clazz) {
	// String str = null;
	// if (StringUtils.isNotEmpty(key) && null != clazz)
	// str = getJedisCluster().get(key);
	// try {
	// if (StringUtils.isNotEmpty(str))
	// return gson.fromJson(str, clazz);
	// } catch (Exception e) {
	// logger.error(e);
	// }
	//
	// return null;
	// }
	//
	// @Override
	// public String get(String key) {
	// if (StringUtils.isNotEmpty(key))
	// return getJedisCluster().get(key);
	// return null;
	// }
	//
	// @Override
	// public <T> List<T> hmget(String key, Class<T> clazz, String... ids) {
	// List<T> results = null;
	// if (StringUtils.isNotEmpty(key) && null != clazz && null != ids &&
	// ids.length > 0) {
	// List<String> vals = getJedisCluster().hmget(key, ids);
	// if (null != vals && vals.size() > 0) {
	// for (String val : vals) {
	// try {
	// if (StringUtils.isNotEmpty(val)) {
	// // T t = objMapper.readValue(val, clazz);
	// T t = gson.fromJson(val, clazz);
	// if (null != t) {
	// if (null == results)
	// results = new ArrayList<T>();
	// results.add(t);
	// }
	// }
	// } catch (Exception e) {
	// logger.error(e);
	// }
	// }
	// }
	// }
	//
	// return results;
	// }
	//
	// @Override
	// public Map<String, String> hgetAll(String key) {
	// if (StringUtils.isNotEmpty(key)) {
	// return getJedisCluster().hgetAll(key);
	// }
	// return null;
	// }
	//
	// @Override
	// public boolean hexists(String key, String field) {
	// if (StringUtils.isNotEmpty(key)) {
	// return getJedisCluster().hexists(key, field);
	// }
	// return false;
	// }
	//
	// @Override
	// public <T> String hmset(String key, Map<String, T> objs) {
	// if (null != key && null != objs && objs.size() > 0) {
	// Map<String, String> map = new HashMap<String, String>();
	// for (String k : objs.keySet()) {
	// T t = objs.get(k);
	// try {
	// map.put(k, gson.toJson(t));
	// // map.put(k, objMapper.writeValueAsString(t));
	// } catch (Exception e) {
	// logger.error(e);
	// }
	// }
	// if (null != map && map.size() > 0)
	// return getJedisCluster().hmset(key, map);
	// }
	//
	// return null;
	// }
	//
	// @Override
	// public Long hset(String key, String hkey, String value) {
	// if (StringUtils.isNotEmpty(key)) {
	// return getJedisCluster().hset(key, hkey, value);
	// }
	// return null;
	// }
	//
	// @Override
	// public <T> Long hset(String key, String hkey, T t) {
	// if (StringUtils.isNotEmpty(key) && null != t) {
	// String value = null;
	// try {
	// value = gson.toJson(t);// objMapper.writeValueAsString(t);
	// return getJedisCluster().hset(key, hkey, value);
	// } catch (Exception e) {
	// logger.error(e);
	// }
	// }
	//
	// return null;
	// }
	//
	// @Override
	// public <T> T hget(String key, String hkey, Class<T> clazz) {
	//
	// String str = null;
	// if (StringUtils.isNotEmpty(key) && null != clazz)
	// str = getJedisCluster().hget(key, hkey);
	// try {
	// if (StringUtils.isNotEmpty(str))
	// return gson.fromJson(str, clazz);// objMapper.readValue(str,
	// // clazz);
	// } catch (Exception e) {
	// logger.error(e);
	// }
	//
	// return null;
	// }
	//
	// @Override
	// public Long hdel(String key, String hkey) {
	// if (StringUtils.isNotEmpty(key) && StringUtils.isNotEmpty(hkey))
	// return getJedisCluster().hdel(key, hkey);
	//
	// return null;
	// }
	//
	// @Override
	// public Long zcount(String key, Double min, Double max) {
	// if (StringUtils.isNotEmpty(key) && null != min && null != max)
	// return getJedisCluster().zcount(key, min, max);
	//
	// return null;
	// }
	//
	// @Override
	// public Set<String> zrevrange(String key, Long start, Long end) {
	// if (StringUtils.isNotEmpty(key) && null != start && null != end)
	// return getJedisCluster().zrevrange(key, start, end);
	// return null;
	// }
	//
	// @Override
	// public Long zrevrank(String key, String member) {
	//
	// if (StringUtils.isNotEmpty(key) && StringUtils.isNotEmpty(member)) {
	// return getJedisCluster().zrevrank(key, member);
	// }
	// return null;
	// }
	//
	// @Override
	// public Set<String> zrevrangeByScore(String key, Double max, Double min,
	// int offset, int count) {
	// if (StringUtils.isNotEmpty(key) && null != min && null != max)
	// return getJedisCluster().zrevrangeByScore(key, max, min, offset, count);
	// return null;
	// }
	//
	// @Override
	// public Set<String> zrangeByScore(String key, Double max, Double min) {
	// if (StringUtils.isNotEmpty(key) && null != min && null != max)
	// return getJedisCluster().zrangeByScore(key, max, min);
	// return null;
	// }
	//
	// @Override
	// public Set<String> zrangeByScore(String key, Double max, Double min, int
	// offset, int count) {
	// return getJedisCluster().zrangeByScore(key, min, max, offset, count);
	// }
	//
	// @Override
	// public Double zincrby(String key, String member, Double score) {
	// if (StringUtils.isNotEmpty(key) && StringUtils.isNotEmpty(member) && null
	// != score)
	// return getJedisCluster().zincrby(key, score, member);
	// return null;
	// }
	//
	// @Override
	// public Long zrank(String key, String member) {
	// if (StringUtils.isNotEmpty(key) && StringUtils.isNotEmpty(member))
	// return getJedisCluster().zrank(key, member);
	// return null;
	// }
	//
	// @Override
	// public Long zcount(String key, String min, String max) {
	// if (StringUtils.isNotEmpty(key))
	// return getJedisCluster().zcount(key, min, max);
	// return null;
	// }
	//
	//
	// @Override
	// public void expire(String key, int seconds) {
	// if (StringUtils.isNotEmpty(key))
	// getJedisCluster().expire(key, seconds);
	// }
	//
	// @Override
	// public long incr(String key) {
	// return getJedisCluster().incr(key);
	// }
	//
	// @Override
	// public long decr(String key) {
	// return getJedisCluster().decr(key);
	// }
	//
	// @Override
	// public long zcard(String key) {
	// return getJedisCluster().zcard(key);
	// }
	//
	// @Override
	// public long setnx(String key, String value) {
	// return getJedisCluster().setnx(key, value);
	// }
	//
	// @Override
	// public Set<Tuple> zrevrangeByScoreWithScores(String key, Double max,
	// Double min, int offset, int count) {
	// return getJedisCluster().zrevrangeByScoreWithScores(key, max, min,
	// offset, count);
	// }
	//
	// @Override
	// public Set<Tuple> zrangeByScoreWithScores(String key, Double max, Double
	// min, int offset, int count) {
	// return getJedisCluster().zrangeByScoreWithScores(key, min, max, offset,
	// count);
	// }
	//
	// @Override
	// public Set<Tuple> zrevrangeWithScores(String key, long start, long end) {
	// return getJedisCluster().zrevrangeWithScores(key, start, end);
	// }
	//
	// @Override
	// public Set<String> zrange(String key, int start, int end) {
	// return getJedisCluster().zrange(key, start, end);
	// }
	//
	// @Override
	// public String setex(String key, int seconds, String value) {
	// return getJedisCluster().setex(key, seconds, value);
	// }
	//
	// @Override
	// public long sadd(String key, String... member) {
	// if (StringUtils.isEmpty(key))
	// return 0;
	//
	// if (ArrayUtils.isEmpty(member))
	// return 0;
	//
	// return getJedisCluster().sadd(key, member);
	// }
	//
	// @Override
	// public long scard(String key) {
	// if (StringUtils.isEmpty(key))
	// return 0;
	//
	// return getJedisCluster().scard(key);
	// }
	//
	// @Override
	// public boolean sismember(final String key, final String member) {
	// return getJedisCluster().sismember(key, member);
	// }
	//
	// @Override
	// public long srem(String key, String... member) {
	// return getJedisCluster().srem(key, member);
	// }
	//
	// @Override
	// public Set<String> sinter(String... keys) {
	// return getJedisCluster().sinter(keys);
	//
	// }

}