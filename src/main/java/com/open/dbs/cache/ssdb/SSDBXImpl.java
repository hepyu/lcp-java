package com.open.dbs.cache.ssdb;

import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.nutz.ssdb4j.spi.Cmd;
import org.nutz.ssdb4j.spi.Response;
import org.nutz.ssdb4j.spi.SSDB;

public class SSDBXImpl implements SSDBX {

	public static final JsonObjectConv jsonConv = new JsonObjectConv();
	private final SSDBHolder ssdbHolder;

	private final byte[] prefix;

	public SSDBXImpl(CacheConfig cfg, String prefix) {
		this.ssdbHolder = new SSDBHolder(cfg, jsonConv);
		this.prefix = prefix == null ? new byte[0] : prefix.getBytes(Charset.forName("utf-8"));
	}

	private SSDBXImpl(SSDBHolder ssdbHolder, String prefix) {
		this.ssdbHolder = ssdbHolder;
		this.prefix = prefix == null ? new byte[0] : prefix.getBytes(Charset.forName("utf-8"));
	}

	SSDBXImpl clone(String prefix) {
		return new SSDBXImpl(this.ssdbHolder, prefix);
	}

	SSDBHolder getSSDBHolder() {
		return this.ssdbHolder;
	}

	public SSDB ssdb() {
		return ssdbHolder.getSsdb();
	}

	// private <K> K dekey(byte[] data, Class<K> clazz) {
	// if (prefix.length == 0) {
	// return jsonConv.toObject(data, clazz);
	// }
	// return jsonConv.toObject(data, prefix.length, data.length - prefix.length, clazz);
	// }

	private <K> byte[] mixkey(K k) {
		SSDBCounterByThread.inc();
		byte[] bts = jsonConv.bytes(k);
		if (prefix.length == 0) {
			return bts;
		}
		byte[] rtnBts = merger(prefix, bts);
		return rtnBts;
	}

	private <K> byte[][] mixkeys(K[] keys) {
		if (prefix.length == 0) {
			return jsonConv.bytess((Object[]) keys);
		}
		byte[][] btsKeys = jsonConv.bytess((Object[]) keys);
		for (int i = 0; i < btsKeys.length; i++) {
			btsKeys[i] = this.merger(prefix, btsKeys[i]);
		}
		return btsKeys;

	}

	private byte[] merger(byte[] bts1, byte[] bts2) {
		byte[] bts3 = new byte[bts1.length + bts2.length];
		System.arraycopy(bts1, 0, bts3, 0, bts1.length);
		System.arraycopy(bts2, 0, bts3, bts1.length, bts2.length);
		return bts3;
	}

	// ############# 基本工具方法 ################
	public long toLong(Response resp) {
		if (resp.ok()) {
			return resp.asLong();
		}
		return -1;
	}

	private byte[] getRespData0(Response resp) {
		if (resp == null)
			return null;
		if (!resp.ok())
			return null;
		if (resp.datas == null || resp.datas.isEmpty())
			return null;
		return resp.datas.get(0);
	}

	private List<byte[]> getRespData(Response resp) {
		if (resp == null)
			return null;
		if (!resp.ok())
			return null;
		if (resp.datas == null || resp.datas.isEmpty())
			return null;
		return resp.datas;
	}

	private <K, V> Map<K, V> getMap(List<byte[]> datas, Class<K> clazzK, Class<V> clazzV) {
		if (datas == null || datas.isEmpty())
			return null;
		if (datas.size() % 2 != 0)
			throw new IllegalArgumentException("not key-value pairs");
		Map<K, V> map = new LinkedHashMap<K, V>();
		Iterator<byte[]> it = datas.iterator();
		while (it.hasNext()) {
			map.put(jsonConv.toObject(it.next(), clazzK), jsonConv.toObject(it.next(), clazzV));
		}
		return map;
	}

	private <K, V> Map<K, V> getKVMap(List<byte[]> datas, Class<K> clazzK, Class<V> clazzV) {
		if (datas == null || datas.isEmpty())
			return null;
		if (datas.size() % 2 != 0)
			throw new IllegalArgumentException("not key-value pairs");
		Map<K, V> map = new LinkedHashMap<K, V>();
		Iterator<byte[]> it = datas.iterator();
		while (it.hasNext()) {
			byte[] data = it.next();
			map.put(jsonConv.toObject(data, prefix.length, data.length - prefix.length, clazzK), jsonConv.toObject(it.next(), clazzV));
		}
		return map;
	}

	private byte[][] add(byte[] first, byte[][] others) {
		final byte[][] bts = new byte[others.length + 1][];
		bts[0] = first;
		for (int i = 0; i < others.length; i++) {
			bts[i + 1] = others[i];
		}
		return bts;
	}

	// ########### 下面全是接口定义 ###########
	// ## 基本接口
	@Override
	public <K, V> long set(K key, V value) {
		return toLong(ssdb().req(Cmd.set, mixkey(key), jsonConv.bytes(value)));
	}

	@Override
	public <K, V> long set(K key, V v, long ttl) {
		return toLong(ssdb().req(Cmd.setx, mixkey(key), jsonConv.bytes(v), jsonConv.bytes(ttl)));
	}

	@Override
	public <K, V> long setx(K key, V v, long ttl) {
		return this.set(key, v, ttl);
	}

	@Override
	public <K, V> long setnx(K key, V v) {
		return toLong(ssdb().req(Cmd.setnx, mixkey(key), jsonConv.bytes(v)));
	}

	@Override
	public <K, V> V get(K key, Class<V> clazz) {
		return jsonConv.toObject(getRespData0(ssdb().req(Cmd.get, mixkey(key))), clazz);
	}

	@Override
	public <K, V> long setRenewal(K key, V v, long step, long ttl) {
		SSDB b = ssdb().batch();
		b.req(Cmd.set, mixkey(key), jsonConv.bytes(new Renewal<V>(System.currentTimeMillis() + ttl * 1000, v)));
		b.req(Cmd.expire, mixkey(key), jsonConv.bytes(ttl));
		return toLong(b.exec().get(1));
	}

	@Override
	public <K> long expired(K key, long ttl) {
		return toLong(ssdb().req(Cmd.expire, mixkey(key), Long.toString(ttl).getBytes()));
	}

	@Override
	public <K, V> Map<K, V> mget(K[] keys, Class<K> clazzK, Class<V> clazzV) {
		Response resp = ssdb().req(Cmd.multi_get, mixkeys(keys));
		List<byte[]> datas = this.getRespData(resp);
		return getKVMap(datas, clazzK, clazzV);
	}

	@Override
	public <K> long incr(K key, long value) {
		Response resp = ssdb().req(Cmd.incr, mixkey(key), jsonConv.bytes(Long.toString(value)));
		byte[] data = this.getRespData0(resp);
		String strValue = jsonConv.toString(data);
		return Long.valueOf(strValue);
	}

	@Override
	public <K> long ttl(K key) {
		return toLong(ssdb().req(Cmd.ttl, mixkey(key)));
	}

	@Override
	public <K> long exists(K key) {
		return toLong(ssdb().req(Cmd.exists, mixkey(key)));
	}

	@Override
	public <K> long del(K key) {
		return toLong(ssdb().req(Cmd.del, mixkey(key)));
	}

	@Override
	public <K> long multi_del(K[] keys) {
		return toLong(ssdb().req(Cmd.multi_del, mixkeys(keys)));
	}

	@Override
	public <K, V> V getRenewal(K key, Class<V> clazz, long step, long ttl) {
		if (step > ttl) {
			step = ttl;
		}
		@SuppressWarnings("unchecked")
		Renewal<V> renewal = this.get(key, Renewal.class);
		if (renewal == null) {
			return null;
		}
		if (renewal.getTick() < System.currentTimeMillis() + step * 1000) {
			renewal.setTick(System.currentTimeMillis() + ttl * 1000);
			SSDB b = ssdb().batch();
			b.req(Cmd.set, mixkey(key), jsonConv.bytes(renewal));
			b.req(Cmd.expire, mixkey(key), jsonConv.bytes(ttl));
			b.exec();
		}
		String json = JsonObjectConv.gson.toJson(renewal.getV());
		return JsonObjectConv.gson.fromJson(json, clazz);
	}

	public <K, V> V getset(K key, V v, Class<V> clazz) {
		return jsonConv.toObject(getRespData0(ssdb().req(Cmd.getset, mixkey(key), jsonConv.bytes(v))), clazz);
	}

	// ## Map相关操作
	@Override
	public <K, HK, V> long hset(K key, HK hkey, V value) {
		return toLong(ssdb().req(Cmd.hset, mixkey(key), jsonConv.bytes(hkey), jsonConv.bytes(value)));
	}

	@Override
	public <K, HK, V> V hget(K key, HK hkey, Class<V> clazz) {
		Response resp = ssdb().req(Cmd.hget, mixkey(key), jsonConv.bytes(hkey));
		byte[] bts = this.getRespData0(resp);
		if (bts == null) {
			return null;
		}
		return jsonConv.toObject(bts, clazz);
	}

	@Override
	public <K, HK, V> Map<HK, V> hgetall(K key, Class<HK> clazzHK, Class<V> clazzV) {
		Response resp = ssdb().req(Cmd.hgetall, mixkey(key));
		List<byte[]> datas = this.getRespData(resp);
		return getMap(datas, clazzHK, clazzV);
	}

	@Override
	public <K, HK, V> Map<HK, V> hmget(K key, HK[] hkeys, Class<HK> clazzHK, Class<V> clazzV) {
		final byte[] btsKey = mixkey(key);
		final byte[][] btsHkeys = jsonConv.bytess((Object[]) hkeys);
		final byte[][] bts = add(btsKey, btsHkeys);
		Response resp = ssdb().req(Cmd.multi_hget, bts);
		List<byte[]> datas = this.getRespData(resp);
		return getMap(datas, clazzHK, clazzV);
	}

	@Override
	public <K, HK> long hincr(K key, HK hkey, long value) {
		Response resp = ssdb().req(Cmd.hincr, mixkey(key), jsonConv.bytes(hkey), jsonConv.bytes(Long.toString(value)));
		byte[] data = this.getRespData0(resp);
		String strValue = jsonConv.toString(data);
		return Long.valueOf(strValue);
	}

	@Override
	public <K, HK, V> long multi_hset(K key, Map<HK, V> values) {
		byte[][] bts = new byte[values.size() * 2 + 1][];
		bts[0] = mixkey(key);
		int index = 1;
		for (Map.Entry<HK, V> e : values.entrySet()) {
			bts[index++] = jsonConv.bytes(e.getKey());
			bts[index++] = jsonConv.bytes(e.getValue());
		}
		return toLong(ssdb().req(Cmd.multi_hset, bts));
	}

	@Override
	public <K, HK> long multi_hdel(K key, HK[] hkeys) {
		byte[][] bts = new byte[hkeys.length + 1][];
		bts[0] = mixkey(key);
		int index = 1;
		for (HK hkey : hkeys) {
			bts[index++] = jsonConv.bytes(hkey);
		}
		return toLong(ssdb().req(Cmd.multi_hdel, bts));
	}

	public <K, HK> long hdel(K key, HK hkey) {
		return toLong(ssdb().req(Cmd.hdel, mixkey(key), jsonConv.bytes(hkey)));
	}

	@Override
	public <K> long hsize(K key) {
		return toLong(ssdb().req(Cmd.hsize, mixkey(key)));
	}

	@Override
	public <K> long hclear(K key) {
		return toLong(ssdb().req(Cmd.hclear, mixkey(key)));
	}

	public <K, HK> long hexists(K key, HK hkey) {
		return toLong(ssdb().req(Cmd.hexists, mixkey(key), jsonConv.bytes(hkey)));
	}

	// ## zsort相关操作
	@Override
	public <K> long zclear(K key) {
		return toLong(ssdb().req(Cmd.zclear, mixkey(key)));
	}

	@Override
	public <K> long zcount(K key, String score_start, String score_end) {
		return toLong(ssdb().req(Cmd.zcount, mixkey(key), jsonConv.bytes(score_start), jsonConv.bytes(score_end)));
	}

	@Override
	public <K, V> long zget(K key, V value) {
		return toLong(ssdb().req(Cmd.zget, mixkey(key), jsonConv.bytes(value)));
	}

	@Override
	public <K, V> long zset(K key, V value, long score) {
		return toLong(ssdb().req(Cmd.zset, mixkey(key), jsonConv.bytes(value), jsonConv.bytes(Long.toString(score))));
	}

	@Override
	public <K, V> long zdel(K key, V v) {
		return toLong(ssdb().req(Cmd.zdel, mixkey(key), jsonConv.bytes(v)));
	}

	@Override
	public <K, V> Map<V, Long> zrange(K key, int offset, int limit, Class<V> clazz) {
		Response resp = ssdb().req(Cmd.zrange, mixkey(key), jsonConv.bytes(Integer.toString(offset)), jsonConv.bytes(Integer.toString(limit)));
		List<byte[]> ds = this.getRespData(resp);
		if (ds == null) {
			return null;
		}
		return this.getMap(ds, clazz, Long.class);
	}

	@Override
	public <K, V> Map<V, Long> zrrange(K key, int offset, int limit, Class<V> clazz) {
		Response resp = ssdb().req(Cmd.zrrange, mixkey(key), jsonConv.bytes(Integer.toString(offset)), jsonConv.bytes(Integer.toString(limit)));
		List<byte[]> ds = this.getRespData(resp);
		if (ds == null) {
			return null;
		}
		return this.getMap(ds, clazz, Long.class);
	}

	@Override
	public <K, V> long zincr(K key, V value, long incr) {
		return toLong(ssdb().req(Cmd.zincr, mixkey(key), jsonConv.bytes(value), jsonConv.bytes(Long.toString(incr))));
	}

	@Override
	public <K, V> long zdecr(K key, V value, long incr) {
		return toLong(ssdb().req(Cmd.zdecr, mixkey(key), jsonConv.bytes(value), jsonConv.bytes(Long.toString(incr))));
	}

	@Override
	public <K> long zsize(K key) {
		return toLong(ssdb().req(Cmd.zsize, mixkey(key)));
	}

	@Override
	public <K, V> long zrank(K key, V value) {
		return toLong(ssdb().req(Cmd.zrank, mixkey(key), jsonConv.bytes(value)));
	}

	@Override
	public <K, V> long zrrank(K key, V value) {
		return toLong(ssdb().req(Cmd.zrrank, mixkey(key), jsonConv.bytes(value)));
	}

	@Override
	public <K, V> long multi_zset(K key, Map<V, Long> values) {
		byte[][] bts = new byte[values.size() * 2 + 1][];
		bts[0] = mixkey(key);
		int index = 1;
		for (Map.Entry<V, Long> e : values.entrySet()) {
			bts[index++] = jsonConv.bytes(e.getKey());
			bts[index++] = jsonConv.bytes(e.getValue());
		}
		return toLong(ssdb().req(Cmd.multi_zset, bts));
	}

	@Override
	public <K, V> Map<V, Long> multi_zget(K key, V[] values, Class<V> clazzV) {
		byte[][] bts = new byte[values.length + 1][];
		bts[0] = mixkey(key);
		int index = 1;
		for (V v : values) {
			bts[index++] = jsonConv.bytes(v);
		}
		Response resp = ssdb().req(Cmd.multi_zget, bts);
		List<byte[]> datas = this.getRespData(resp);
		if (datas == null)
			return null;
		return this.getMap(datas, clazzV, Long.class);
	}

	@Override
	public <K> long zremrangebyrank(K key, int start, int end) {
		return toLong(ssdb().req(Cmd.zremrangebyrank, mixkey(key), jsonConv.bytes(String.valueOf(start)), jsonConv.bytes(String.valueOf(end))));
	}

	@Override
	public <K, V> Map<V, Long> zscan(K key, V value_start, String score_start, String score_end, long limit, Class<V> clazz) {
		Response resp = ssdb().req(Cmd.zscan//
				, mixkey(key)//
				, jsonConv.bytes(value_start)//
				, jsonConv.bytes(score_start)//
				, jsonConv.bytes(score_end)//
				, jsonConv.bytes(limit));
		List<byte[]> datas = this.getRespData(resp);
		if (datas == null)
			return null;
		return this.getMap(datas, clazz, Long.class);
	}

	@Override
	public <K, V> Map<V, Long> zrscan(K key, V value_start, String score_start, String score_end, long limit, Class<V> clazz) {
		Response resp = ssdb().req(Cmd.zrscan//
				, mixkey(key)//
				, jsonConv.bytes(value_start)//
				, jsonConv.bytes(score_start)//
				, jsonConv.bytes(score_end)//
				, jsonConv.bytes(limit));
		List<byte[]> datas = this.getRespData(resp);
		if (datas == null)
			return null;
		return this.getMap(datas, clazz, Long.class);
	}

	@Override
	public <K, V> long zexists(K key, V value) {
		return toLong(ssdb().req(Cmd.zexists, mixkey(key), jsonConv.bytes(value)));
	}

}
