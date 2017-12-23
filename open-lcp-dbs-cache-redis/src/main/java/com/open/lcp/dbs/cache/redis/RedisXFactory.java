package com.open.lcp.dbs.cache.redis;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gson.Gson;
import com.open.lcp.core.env.LcpResource;
import com.open.lcp.core.env.finder.EnvEnum;
import com.open.lcp.core.env.finder.EnvFinder;
import com.open.lcp.core.env.finder.ZKFinder;
import com.open.lcp.core.register.mangocity.zk.ConfigChangeListener;
import com.open.lcp.core.register.mangocity.zk.ConfigChangeSubscriber;
import com.open.lcp.core.register.mangocity.zk.ZkConfigChangeSubscriberImpl;
import com.open.lcp.dbs.cache.redis.cluster.JedisClusterImpl;
import com.open.lcp.dbs.cache.redis.single.JedisPoolImpl;

public class RedisXFactory {
	private static final Log logger = LogFactory.getLog(RedisXFactory.class);

	private static final ConcurrentMap<String, RedisX> redisMap = new ConcurrentHashMap<String, RedisX>();

	private static final Object INIT_REDISIMPL_MAP = new Object();

	public static RedisX loadRedisX(final LcpResource lcpResource) {
		//String zkAbsolutePath = lcpResource.getAbsolutePath(EnvFinder.getProfile());
		
		RedisX instance = redisMap.get(lcpResource.getClassDeclaredDataSourceAnnotationName());
		if (instance == null) {
			synchronized (INIT_REDISIMPL_MAP) {
				instance = redisMap.get(lcpResource.getClassDeclaredDataSourceAnnotationName());
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
								lcpResource.getAbsoluteParentPath(EnvFinder.getProfile()));
						sub.subscribe(lcpResource.getNodeName(), new ConfigChangeListener() {

							@Override
							public void configChanged(String key, String value) {
								RedisX redisX = loadRedisX(lcpResource, value);

								RedisX old = redisMap.get(lcpResource.getClassDeclaredDataSourceAnnotationName());
								redisMap.put(lcpResource.getClassDeclaredDataSourceAnnotationName(), redisX);
								
								old.close();
							}
						});

						RedisX redisX = loadRedisX(lcpResource, zkClient);
						redisMap.put(lcpResource.getClassDeclaredDataSourceAnnotationName(), redisX);
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
			for (Entry<String, RedisX> entry : redisMap.entrySet()) {
				logger.debug("--RedisServiceImpl--" + entry.getKey() + "--" + entry.getValue());
			}
		}

		return redisMap.get(lcpResource.getClassDeclaredDataSourceAnnotationName());
	}

	private static RedisX loadRedisX(final LcpResource zkResourcePath, String jsonStr) {
		ZKRedisConfig redisConfig = loadZKRedisConfig(jsonStr);
		return loadRedisX(zkResourcePath, redisConfig);
	}

	private static RedisX loadRedisX(final LcpResource zkResourcePath, ZkClient zkClient) {
		String ssdbStr = zkClient.readData(zkResourcePath.getAbsolutePath(EnvFinder.getProfile()));
		ZKRedisConfig redisConfig = loadZKRedisConfig(ssdbStr);
		return loadRedisX(zkResourcePath, redisConfig);
	}

	private static RedisX loadRedisX(final LcpResource zkResourcePath, ZKRedisConfig redisConfig) {
		RedisX redisX = null;
		if (redisConfig.isCluster()) {
			redisX = new JedisClusterImpl(redisConfig);
		} else {
			redisX = new JedisPoolImpl(redisConfig);
		}

		redisX = (RedisX) new RedisProxy(redisX).getProxyInstance();
		return redisX;
	}

	private static ZKRedisConfig loadZKRedisConfig(String jsonStr) {
		Gson gson = new Gson();
		ZKRedisConfig ssdbConfig = gson.fromJson(jsonStr, ZKRedisConfig.class);
		return ssdbConfig;
	}
}
