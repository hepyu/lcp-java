package com.open.dbs.cache.ssdb;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

public class SSDBXFactory {

	private static final Log logger = LogFactory.getLog(SSDBXFactory.class);

	private static final Map<String, SSDBXImpl> ssdbxMap = new ConcurrentHashMap<String, SSDBXImpl>();

	private static final Object LOCK_OF_NEWPATH = new Object();

	public static SSDBX getSSDBX(final String instanceName) {
		final String ssdbZkRoot = ZKFinder.findSSDBZKRoot();
		SSDBXImpl ssdbxImpl = ssdbxMap.get(instanceName);
		if (ssdbxImpl == null) {
			synchronized (LOCK_OF_NEWPATH) {
				if (ssdbxImpl == null) {
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

						ConfigChangeSubscriber sub = new ZkConfigChangeSubscriberImpl(zkClient, ssdbZkRoot);
						sub.subscribe(instanceName, new ConfigChangeListener() {

							@Override
							public void configChanged(String key, String value) {

								ZKSSDBConfig ssdbConfig = loadSSDBCacheConfig(value);
								ssdbxMap.get(instanceName).getSSDBHolder().setSSDBConfig(ssdbConfig);

							}
						});
						// String initValue = sub.getInitValue(source);

						// {"ip":"123.57.204.187","port":"8888","timeout":"200","cfg":{"maxActive":"100","testWhileIdle":true}}
						ZKSSDBConfig ssdbConfig = loadSSDBCacheConfig(zkClient, ssdbZkRoot, instanceName);
						ssdbxMap.put(instanceName, new SSDBXImpl(ssdbConfig));
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
		return ssdbxMap.get(instanceName);
	}

	private static ZKSSDBConfig loadSSDBCacheConfig(ZkClient zkClient, String ssdbZkRoot, String key) {
		String ssdbStr = zkClient.readData(ssdbZkRoot + "/" + key);
		return loadSSDBCacheConfig(ssdbStr);
	}

	private static ZKSSDBConfig loadSSDBCacheConfig(String jsonStr) {
		Gson gson = new Gson();
		ZKSSDBConfig ssdbConfig = gson.fromJson(jsonStr, ZKSSDBConfig.class);
		return ssdbConfig;
	}

}
