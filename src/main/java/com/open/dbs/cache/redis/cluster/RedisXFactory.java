package com.open.dbs.cache.redis.cluster;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gson.Gson;
import com.mangocity.zk.ConfigChangeListener;
import com.mangocity.zk.ConfigChangeSubscriber;
import com.mangocity.zk.ZkConfigChangeSubscriberImpl;
import com.open.env.finder.ZKFinder;

public class RedisXFactory {
	private static final Log logger = LogFactory.getLog(RedisXFactory.class);

	private static final ConcurrentMap<String, RedisX> redisMap = new ConcurrentHashMap<String, RedisX>();

	private static final Object INIT_REDISIMPL_MAP = new Object();

	private static final String PREFIX_DEFAULE = "";

	public static RedisX loadRedisX(final String source, String prefix) {
		if (prefix == null) {
			prefix = PREFIX_DEFAULE;
		}

		final String redisZKRoot = ZKFinder.findRedisZKRoot();
		RedisX instance = redisMap.get(source);
		if (instance == null) {
			synchronized (INIT_REDISIMPL_MAP) {
				instance = redisMap.get(source);
				if (instance == null) {
					ZkClient zkClient = new ZkClient(ZKFinder.findZKHosts(), 10000, 10000, new ZkSerializer() {

						@Override
						public byte[] serialize(Object paramObject) throws ZkMarshallingError {
							return paramObject == null ? null : paramObject.toString().getBytes();
						}

						@Override
						public Object deserialize(byte[] paramArrayOfByte) throws ZkMarshallingError {
							return new String(paramArrayOfByte);
						}
					});

					ConfigChangeSubscriber sub = new ZkConfigChangeSubscriberImpl(zkClient, redisZKRoot);
					sub.subscribe(source, new ConfigChangeListener() {

						@Override
						public void configChanged(String key, String value) {

							ZKRedisConfig redisConfig = loadZKRedisConfig(value);
							redisMap.get(source).getHolder().setJedisCluster(redisConfig.getServer().get(0));

						}
					});

					ZKRedisConfig redisConfig = loadZKRedisConfig(zkClient, redisZKRoot, source);
					redisMap.put(source, new RedisXImpl(redisConfig.getServer().get(0)));
				}
			}
		}
		if (redisMap != null && redisMap.size() > 0) {
			for (Entry<String, RedisX> entry : redisMap.entrySet()) {
				logger.debug("--RedisServiceImpl--" + entry.getKey() + "--" + entry.getValue() + "--"
						+ entry.getValue().getHolder().getJedisCluster());
			}
		}

		return redisMap.get(source);
	}

	private static ZKRedisConfig loadZKRedisConfig(ZkClient zkClient, String ssdbZkRoot, String key) {
		String ssdbStr = zkClient.readData(ssdbZkRoot + "/" + key);
		return loadZKRedisConfig(ssdbStr);
	}

	private static ZKRedisConfig loadZKRedisConfig(String jsonStr) {
		Gson gson = new Gson();
		ZKRedisConfig ssdbConfig = gson.fromJson(jsonStr, ZKRedisConfig.class);
		return ssdbConfig;
	}
}
