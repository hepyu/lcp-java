package com.open.dbs.cache.redis;

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
import com.open.dbs.cache.redis.cluster.JedisClusterImpl;
import com.open.dbs.cache.redis.single.JedisPoolImpl;
import com.open.env.finder.ZKFinder;
import com.open.lcp.LcpResource;

public class RedisXFactory {
	private static final Log logger = LogFactory.getLog(RedisXFactory.class);

	private static final ConcurrentMap<LcpResource, RedisX> redisMap = new ConcurrentHashMap<LcpResource, RedisX>();

	private static final Object INIT_REDISIMPL_MAP = new Object();

	public static RedisX loadRedisX(final LcpResource zkResourcePath) {

		RedisX instance = redisMap.get(zkResourcePath);
		if (instance == null) {
			synchronized (INIT_REDISIMPL_MAP) {
				instance = redisMap.get(zkResourcePath);
				if (instance == null) {
					ZkClient zkClient = null;
					try {
						zkClient = new ZkClient(ZKFinder.findZKHosts(), 180000, 180000, new ZkSerializer() {

							@Override
							public byte[] serialize(Object paramObject) throws ZkMarshallingError {
								return paramObject == null ? null : paramObject.toString().getBytes();
							}

							@Override
							public Object deserialize(byte[] paramArrayOfByte) throws ZkMarshallingError {
								return new String(paramArrayOfByte);
							}
						});

						ConfigChangeSubscriber sub = new ZkConfigChangeSubscriberImpl(zkClient,
								ZKFinder.findZKResourceParentPath(zkResourcePath));
						sub.subscribe(zkResourcePath.zkNodeName(), new ConfigChangeListener() {

							@Override
							public void configChanged(String key, String value) {
								loadRedisX(zkResourcePath, value);
							}
						});

						loadRedisX(zkResourcePath, zkClient);
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
						System.exit(-1);
					} finally {
						if (zkClient != null) {
							zkClient.close();
						}
					}
				}
			}
		}
		if (redisMap != null && redisMap.size() > 0) {
			for (Entry<LcpResource, RedisX> entry : redisMap.entrySet()) {
				logger.debug("--RedisServiceImpl--" + entry.getKey() + "--" + entry.getValue());
			}
		}

		return redisMap.get(zkResourcePath);
	}

	private static void loadRedisX(final LcpResource zkResourcePath, String jsonStr) {
		ZKRedisConfig redisConfig = loadZKRedisConfig(jsonStr);
		loadRedisX(zkResourcePath, redisConfig);
	}

	private static void loadRedisX(final LcpResource zkResourcePath, ZkClient zkClient) {
		String ssdbStr = zkClient.readData(ZKFinder.findAbsoluteZKResourcePath(zkResourcePath));
		ZKRedisConfig redisConfig = loadZKRedisConfig(ssdbStr);
		loadRedisX(zkResourcePath, redisConfig);
	}

	private static void loadRedisX(final LcpResource zkResourcePath, ZKRedisConfig redisConfig) {
		RedisX redisX = null;
		if (redisConfig.isCluster()) {
			redisX = new JedisClusterImpl(redisConfig);
		} else {
			redisX = new JedisPoolImpl(redisConfig);
		}

		redisX = (RedisX) new RedisProxy(redisX).getProxyInstance();
		redisMap.put(zkResourcePath, redisX);
	}

	private static ZKRedisConfig loadZKRedisConfig(String jsonStr) {
		Gson gson = new Gson();
		ZKRedisConfig ssdbConfig = gson.fromJson(jsonStr, ZKRedisConfig.class);
		return ssdbConfig;
	}
}
