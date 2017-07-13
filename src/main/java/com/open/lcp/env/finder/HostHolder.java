package com.open.lcp.env.finder;

import java.util.List;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gson.Gson;
import com.open.lcp.env.finder.config.HostsConfig;
import com.open.lcp.mangocity.zk.ConfigChangeListener;
import com.open.lcp.mangocity.zk.ConfigChangeSubscriber;
import com.open.lcp.mangocity.zk.ZkConfigChangeSubscriberImpl;

public class HostHolder {

	private static final Log logger = LogFactory.getLog(HostHolder.class);

	private static List<String> devHosts;

	private static List<String> testHosts;

	private static List<String> preHosts;

	private static List<String> productHosts;

	private static volatile boolean hasPrepared = false;

	private static void load() {
		ZkClient zkClient = null;
		try {
			zkClient = new ZkClient(ZKFinder.findZKHosts(), 10000, 10000, new ZkSerializer() {

				@Override
				public byte[] serialize(Object paramObject) throws ZkMarshallingError {
					return paramObject == null ? null : paramObject.toString().getBytes();
				}

				@Override
				public Object deserialize(byte[] paramArrayOfByte) throws ZkMarshallingError {
					return new String(paramArrayOfByte);
				}
			});

			for (final EnvEnum envEnum : EnvEnum.values()) {
				String envRoot = EnvConsts.ENV_ROOT + "/" + envEnum.name();
				boolean isExist = zkClient.exists(envRoot + "/" + EnvConsts.KEY_HOSTS);
				if (!isExist) {
					continue;
				}

				String value = zkClient.readData(envRoot + "/" + EnvConsts.KEY_HOSTS);
				Gson gson = new Gson();
				HostsConfig hostsConfig = gson.fromJson(value, HostsConfig.class);
				if (envEnum == EnvEnum.dev) {
					devHosts = hostsConfig.getHosts();
				} else if (envEnum == EnvEnum.test) {
					testHosts = hostsConfig.getHosts();
				} else if (envEnum == EnvEnum.pre) {
					preHosts = hostsConfig.getHosts();
				} else if (envEnum == EnvEnum.product) {
					productHosts = hostsConfig.getHosts();
				}

				ConfigChangeSubscriber sub = new ZkConfigChangeSubscriberImpl(zkClient, envRoot);
				sub.subscribe(EnvConsts.KEY_HOSTS, new ConfigChangeListener() {
					@Override
					public void configChanged(String key, String value) {
						Gson gson = new Gson();
						HostsConfig hostsConfig = gson.fromJson(value, HostsConfig.class);
						if (envEnum == EnvEnum.dev) {
							devHosts = hostsConfig.getHosts();
						} else if (envEnum == EnvEnum.test) {
							testHosts = hostsConfig.getHosts();
						} else if (envEnum == EnvEnum.pre) {
							preHosts = hostsConfig.getHosts();
						} else if (envEnum == EnvEnum.product) {
							productHosts = hostsConfig.getHosts();
						}
					}
				});
			}

			hasPrepared = true;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			System.exit(-1);
		} finally {
			if (zkClient != null) {
				zkClient.close();
			}
		}
	}

	public static EnvEnum findProfile(String host) throws Exception {
		if (!hasPrepared) {
			synchronized (HostHolder.class) {
				if (!hasPrepared) {
					load();
				}
			}
		}
		if (devHosts.contains(host)) {
			return EnvEnum.dev;
		} else if (testHosts.contains(host)) {
			return EnvEnum.test;
		} else if (preHosts.contains(host)) {
			return EnvEnum.pre;
		} else if (productHosts.contains(host)) {
			return EnvEnum.product;
		}
		throw new Error("no env exist this host:" + host);
	}

}
