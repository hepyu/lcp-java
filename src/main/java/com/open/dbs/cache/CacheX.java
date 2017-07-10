package com.open.dbs.cache;

import java.util.List;
import java.util.Map;

import redis.clients.jedis.GeoRadiusResponse;
import redis.clients.jedis.GeoUnit;
import redis.clients.jedis.params.geo.GeoRadiusParam;

public interface CacheX {

	// geo

	public <K, MEMBER> long geoAdd(K key, double longitude, double latitude, MEMBER member);

	public <K> List<GeoRadiusResponse> geoRadius(K key, double longitude, double latitude, double radius, GeoUnit unit,
			GeoRadiusParam param);

	// ## 基本功能

	public <K, V> long set(K key, V v);

	public <K, V> long set(K key, V v, int seconds);

	public <K, V> long setx(K key, V v, int seconds);

	public <K, V> long setnx(K key, V v);

	public <K, V> V get(K key, Class<V> clazz);

	public <K, V> long setRenewal(K key, V v, long step, int seconds);

	public <K, V> V getRenewal(K key, Class<V> clazz, long step, int seconds);

	public <K, V> Map<K, V> mget(K[] keys, Class<K> clazzK, Class<V> clazzV);

	public <K> long incr(K key, long value);

	public <K> long expired(K key, int seconds);

	public <K> int seconds(K key);

	public <K> long exists(K key);

	public <K, V> V getset(K key, V v, Class<V> clazz);

	/**
	 * 只能删除kv结果数据，不支持其它类型。无论成功失败，返回值永远是1
	 * 
	 * @param key
	 * @return 1
	 */
	public <K> long del(K key);

	public <K> long multi_del(K[] keys);

	// ## map相关操作
	public <K, HK, V> long hset(K key, HK hkey, V value);

	public <K, HK, V> V hget(K key, HK hkey, Class<V> clazz);

	public <K, HK, V> Map<HK, V> hmget(K key, HK[] hkeys, Class<HK> clazzHK, Class<V> clazzV);

	public <K, HK, V> Map<HK, V> hgetall(K key, Class<HK> clazzHK, Class<V> clazzV);

	public <K, HK> long hincr(K key, HK hkey, long value);

	public <K, HK, V> long multi_hset(K key, Map<HK, V> values);

	public <K, HK> long multi_hdel(K key, HK[] hkeys);

	public <K, HK> long hdel(K key, HK hkey);

	public <K> long hsize(K key);

	public <K> long hclear(K key);

	public <K, HK> long hexists(K key, HK hkey);

	// ## z相关操作
	public <K> long zclear(K key);

	public <K> long zcount(K key, String score_start, String score_end);

	public <K, V> long zget(K key, V value);

	public <K, V> long zset(K key, V value, long score);

	public <K, V> long zdel(K key, V value);

	public <K, V> Map<V, Long> zrange(K key, int offset, int limit, Class<V> clazz);

	public <K, V> Map<V, Long> zrrange(K key, int offset, int limit, Class<V> clazz);

	public <K, V> long zrank(K key, V value);

	public <K, V> long zrrank(K key, V value);

	public <K, V> long zincr(K key, V value, long incr);

	public <K> long zsize(K key);

	public <K, V> long zdecr(K key, V value, long incr);

	public <K, V> long multi_zset(K key, Map<V, Long> values);

	public <K, V> Map<V, Long> multi_zget(K key, V[] values, Class<V> clazzV);

	public <K> long zremrangebyrank(K key, int start, int end);

	public <K, V> Map<V, Long> zscan(K key, V value_start, String score_start, String score_end, long limit,
			Class<V> clazz);

	public <K, V> Map<V, Long> zrscan(K key, V value_start, String score_start, String score_end, long limit,
			Class<V> clazz);

	public <K, V> long zexists(K key, V value);

}
