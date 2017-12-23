package com.open.lcp.dbs.cache.ssdb;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gson.Gson;
import com.open.lcp.core.env.LcpResource;
import com.open.lcp.core.env.finder.EnvFinder;
import com.open.lcp.core.env.finder.ZKFinder;
import com.open.lcp.core.register.mangocity.zk.ConfigChangeListener;
import com.open.lcp.core.register.mangocity.zk.ConfigChangeSubscriber;
import com.open.lcp.core.register.mangocity.zk.ZkConfigChangeSubscriberImpl;

public class SSDBXFactory {

	private static final Log logger = LogFactory.getLog(SSDBXFactory.class);

	private static final Map<String, SSDBXImpl> ssdbxMap = new ConcurrentHashMap<String, SSDBXImpl>();

	private static final Object LOCK_OF_NEWPATH = new Object();

	public static SSDBX getSSDBX(final LcpResource lcpResource) {
		//String zkAbsolutePath = lcpResource.getAbsolutePath(EnvFinder.getProfile());
		SSDBXImpl ssdbxImpl = ssdbxMap.get(lcpResource.getClassDeclaredDataSourceAnnotationName());
		if (ssdbxImpl == null) {
			ssdbxImpl = ssdbxMap.get(lcpResource.getClassDeclaredDataSourceAnnotationName());
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
								lcpResource.getAbsoluteParentPath(EnvFinder.getProfile()));
						sub.subscribe(lcpResource.getNodeName(), new ConfigChangeListener() {

							@Override
							public void configChanged(String key, String value) {

								ZKSSDBConfig ssdbConfig = loadSSDBCacheConfig(value);
								ssdbxMap.get(lcpResource.getClassDeclaredDataSourceAnnotationName()).getSSDBHolder().setSSDBConfig(ssdbConfig);

							}
						});
						// String initValue = sub.getInitValue(source);

						// {"ip":"123.57.204.187","port":"8888","timeout":"200","cfg":{"maxActive":"100","testWhileIdle":true}}
						ZKSSDBConfig ssdbConfig = loadSSDBCacheConfig(lcpResource, zkClient);
						ssdbxMap.put(lcpResource.getClassDeclaredDataSourceAnnotationName(), new SSDBXImpl(ssdbConfig));
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
		return ssdbxMap.get(lcpResource.getClassDeclaredDataSourceAnnotationName());
	}

	private static ZKSSDBConfig loadSSDBCacheConfig(LcpResource lcpResource, ZkClient zkClient) {
		String ssdbStr = zkClient.readData(lcpResource.getAbsolutePath(EnvFinder.getProfile()));
		return loadSSDBCacheConfig(ssdbStr);
	}

	private static ZKSSDBConfig loadSSDBCacheConfig(String jsonStr) {
		Gson gson = new Gson();
		ZKSSDBConfig ssdbConfig = gson.fromJson(jsonStr, ZKSSDBConfig.class);
		return ssdbConfig;
	}

}
