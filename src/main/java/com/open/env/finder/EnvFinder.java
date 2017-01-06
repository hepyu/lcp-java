package com.open.env.finder;

import java.net.InetAddress;

import com.open.dbs.cache.ssdb.SSDBLoader;
import com.open.dbs.cache.ssdb.SSDBX;

public class EnvFinder {

	// 1.find zk hosts
	public static String findZKHosts() {
		return EnvConsts.ZK_SERVERS;
	}

	// 2.find profile
	public static EnvEnum findProfile() {
		try {
			InetAddress addr = InetAddress.getLocalHost();
			String ipPrefix = addr.getHostAddress();
			if (ipPrefix.startsWith("192.168.")) {
				return EnvEnum.dev;
			}
			return HostHolder.findProfile(addr.getHostAddress());
		} catch (Exception e) {
			throw new Error("no env exist this host:");
		}
	}

	public static String findProfile(String host) throws Exception {
		return HostHolder.findProfile(host).name();
	}

	// 3.find ssdb

	public static String findSSDBZKRoot() {
		return EnvConsts.ENV_ROOT + "/" + EnvFinder.findProfile().name() + "/ssdb";
	}

	public static SSDBX findSSDBX(String source) {
		return SSDBLoader.loadSSDBX(source, null);
	}

	public static SSDBX findSSDBX(String source, String prefix) {
		return SSDBLoader.loadSSDBX(source, prefix);
	}

}
