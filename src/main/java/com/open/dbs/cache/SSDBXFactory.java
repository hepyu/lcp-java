package com.open.dbs.cache;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bchbc.dbs.env.cfg.DataChangeListener;
import com.bchbc.dbs.zookeeper.ZKFinder;

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
					final ZkCacheCfg zkCacheCfg = ZKFinder.addDataChangeListener(path, new DataChangeListener<ZkCacheCfg>() {

						@Override
						public void onchange(ZkCacheCfg zkcfg) {
							final List<CacheConfig> lsCfg = zkcfg.getServer();
							if (lsCfg == null || lsCfg.size() != 1) {
								logger.error("McpCacheXFactory: ZKFinder get error cfg@" + path);
								return;
							}
							final CacheConfig cfg = lsCfg.get(0);
							ssdbxMap.get(source).get(PREFIX_DEFAULE).getSSDBHolder().setSsdb(cfg);
						}
					}, ZkCacheCfg.class);
					map.put(PREFIX_DEFAULE, new SSDBXImpl(zkCacheCfg.getServer().get(0), PREFIX_DEFAULE));
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
