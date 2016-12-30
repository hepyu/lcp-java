package com.open.dbs.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mangocity.zk.ConfigChangeListener;
import com.mangocity.zk.ConfigChangeSubscriber;
import com.mangocity.zk.ZkConfigChangeSubscriberImpl;

public class SSDBXFactory {

	private static final String PREFIX_DEFAULE = "";
	private static final Log logger = LogFactory.getLog(SSDBXFactory.class);

	private static final Map<String, Map<String, SSDBXImpl>> ssdbxMap = new ConcurrentHashMap<String, Map<String, SSDBXImpl>>();
	private static final String ZK_ROOT = "/com/bchbc/zk/ssdb/";// "/com/bchbc/dbs/cache/";

	private static String getZKPath(String source) {
		return ZK_ROOT + source;
	}

	private static final Object LOCK_OF_NEWPATH = new Object();

	public static SSDBX getSSDBX(final String source, String prefix) {
		if (prefix == null) {
			prefix = PREFIX_DEFAULE;
		}
		Map<String, SSDBXImpl> map = ssdbxMap.get(source);
		if (map == null) {
			synchronized (LOCK_OF_NEWPATH) {
				map = ssdbxMap.get(source);
				if (map == null) {
					map = new ConcurrentHashMap<String, SSDBXImpl>();
					final String path = getZKPath(source);

					// TODO ZkClient需要补充
					ZkClient zkClient = new ZkClient("");
					ConfigChangeSubscriber sub = new ZkConfigChangeSubscriberImpl(zkClient, path);
					sub.subscribe(path, new ConfigChangeListener() {

						@Override
						public void configChanged(String key, String value) {
							
							//TODO value需要包装秤CacheConfig
							final CacheConfig cfg = new CacheConfig();
							ssdbxMap.get(source).get(PREFIX_DEFAULE).getSSDBHolder().setSsdb(cfg);

						}
					});

					// TODO cacheConfig 需要补充
					map.put(PREFIX_DEFAULE, new SSDBXImpl(new CacheConfig(), PREFIX_DEFAULE));
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

}
