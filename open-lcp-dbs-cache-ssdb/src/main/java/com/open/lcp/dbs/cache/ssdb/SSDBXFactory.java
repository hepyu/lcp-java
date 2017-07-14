package com.open.lcp.dbs.cache.ssdb;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gson.Gson;
import com.open.lcp.core.base.LcpResource;
import com.open.lcp.env.finder.ZKFinder;
import com.open.lcp.mangocity.zk.ConfigChangeListener;
import com.open.lcp.mangocity.zk.ConfigChangeSubscriber;
import com.open.lcp.mangocity.zk.ZkConfigChangeSubscriberImpl;

public class SSDBXFactory {

	private static final Log logger = LogFactory.getLog(SSDBXFactory.class);

	private static final Map<LcpResource, SSDBXImpl> ssdbxMap = new ConcurrentHashMap<LcpResource, SSDBXImpl>();

	private static final Object LOCK_OF_NEWPATH = new Object();

	public static SSDBX getSSDBX(final LcpResource zkResourcePath) {
		SSDBXImpl ssdbxImpl = ssdbxMap.get(zkResourcePath);
		if (ssdbxImpl == null) {
			ssdbxImpl = ssdbxMap.get(zkResourcePath);
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

						ConfigChangeSubscriber sub = new ZkConfigChangeSubscriberImpl(zkClient,
								ZKFinder.findAbsoluteZKResourcePath(zkResourcePath));
						sub.subscribe(zkResourcePath.zkNodeName(), new ConfigChangeListener() {

							@Override
							public void configChanged(String key, String value) {

								ZKSSDBConfig ssdbConfig = loadSSDBCacheConfig(value);
								ssdbxMap.get(zkResourcePath).getSSDBHolder().setSSDBConfig(ssdbConfig);

							}
						});
						// String initValue = sub.getInitValue(source);

						// {"ip":"123.57.204.187","port":"8888","timeout":"200","cfg":{"maxActive":"100","testWhileIdle":true}}
						ZKSSDBConfig ssdbConfig = loadSSDBCacheConfig(zkResourcePath, zkClient);
						ssdbxMap.put(zkResourcePath, new SSDBXImpl(ssdbConfig));
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
		return ssdbxMap.get(zkResourcePath);
	}

	private static ZKSSDBConfig loadSSDBCacheConfig(LcpResource zkResourcePath, ZkClient zkClient) {
		String ssdbStr = zkClient.readData(ZKFinder.findAbsoluteZKResourcePath(zkResourcePath));
		return loadSSDBCacheConfig(ssdbStr);
	}

	private static ZKSSDBConfig loadSSDBCacheConfig(String jsonStr) {
		Gson gson = new Gson();
		ZKSSDBConfig ssdbConfig = gson.fromJson(jsonStr, ZKSSDBConfig.class);
		return ssdbConfig;
	}

}
