package com.open.dbs.cache.ssdb;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;

import com.google.gson.Gson;
import com.mangocity.zk.ConfigChangeListener;
import com.mangocity.zk.ConfigChangeSubscriber;
import com.mangocity.zk.ZkConfigChangeSubscriberImpl;
import com.open.env.finder.ZKFinder;

public class SSDBXFactory {

	private static final String PREFIX_DEFAULE = "";
	// private static final Log logger = LogFactory.getLog(SSDBXFactory.class);

	private static final Map<String, Map<String, SSDBXImpl>> ssdbxMap = new ConcurrentHashMap<String, Map<String, SSDBXImpl>>();

	private static final Object LOCK_OF_NEWPATH = new Object();

	public static SSDBX getSSDBX(final String source, String prefix) {
		if (prefix == null) {
			prefix = PREFIX_DEFAULE;
		}

		final String ssdbZkRoot = ZKFinder.findSSDBZKRoot();
		Map<String, SSDBXImpl> map = ssdbxMap.get(source);
		if (map == null) {
			synchronized (LOCK_OF_NEWPATH) {
				map = ssdbxMap.get(source);
				if (map == null) {
					map = new ConcurrentHashMap<String, SSDBXImpl>();

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

					ConfigChangeSubscriber sub = new ZkConfigChangeSubscriberImpl(zkClient, ssdbZkRoot);
					sub.subscribe(source, new ConfigChangeListener() {

						@Override
						public void configChanged(String key, String value) {

							ZKSSDBConfig ssdbConfig = loadSSDBCacheConfig(value);
							ssdbxMap.get(source).get(PREFIX_DEFAULE).getSSDBHolder().setSsdb(ssdbConfig);

						}
					});
					// String initValue = sub.getInitValue(source);

					// {"ip":"123.57.204.187","port":"8888","timeout":"200","cfg":{"maxActive":"100","testWhileIdle":true}}
					ZKSSDBConfig ssdbConfig = loadSSDBCacheConfig(zkClient, ssdbZkRoot, source);
					map.put(PREFIX_DEFAULE, new SSDBXImpl(ssdbConfig, PREFIX_DEFAULE));
					ssdbxMap.put(source, map);
				}
			}
		}
		SSDBXImpl h = map.get(prefix);
		if (h == null) {
			h = map.get(PREFIX_DEFAULE).clone(prefix);
			map.put(prefix, h);
		}
		return h;
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
