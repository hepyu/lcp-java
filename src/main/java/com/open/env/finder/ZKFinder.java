package com.open.env.finder;

import com.open.dbs.cache.ssdb.SSDBLoader;
import com.open.dbs.cache.ssdb.SSDBX;

public class ZKFinder {

	// 1.find zk hosts
	public static String findZKHosts() {
		return EnvConsts.ZK_SERVERS;
	}

	// 2.find ssdb

	public static String findSSDBZKRoot() {
		return EnvConsts.ENV_ROOT + "/" + EnvFinder.findProfile().name() + "/ssdb";
	}

	public static SSDBX findSSDBX(String source) {
		return SSDBLoader.loadSSDBX(source);
	}

	// 3.find redis

	public static String findRedisZKRoot() {
		return EnvConsts.ENV_ROOT + "/" + EnvFinder.findProfile().name() + "/redis";
	}

	// 4.find kafka

	public static String findKafkaZKRoot() {
		return EnvConsts.ENV_ROOT + "/" + EnvFinder.findProfile().name() + "/kafka";
	}
}
