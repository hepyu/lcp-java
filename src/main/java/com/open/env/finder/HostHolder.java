package com.open.env.finder;

import java.util.List;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;

import com.google.gson.Gson;
import com.mangocity.zk.ConfigChangeListener;
import com.mangocity.zk.ConfigChangeSubscriber;
import com.mangocity.zk.ZkConfigChangeSubscriberImpl;
import com.open.env.finder.config.HostsConfig;

public class HostHolder {

	private static List<String> devHosts;

	private static List<String> testHosts;

	private static List<String> preHosts;

	private static List<String> productHosts;

	static {
		ZkClient zkClient = new ZkClient(EnvFinder.findZKHosts(), 10000, 10000, new ZkSerializer() {

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
	}

	public static EnvEnum findProfile(String host) {
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