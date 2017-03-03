package com.open.env.finder;

import com.open.dbs.DBConfig;
import com.open.dbs.cache.ssdb.SSDBLoader;
import com.open.dbs.cache.ssdb.SSDBX;
import com.open.dbs.mysql.MysqlLoader;

public class ZKFinder {

	// 0.find mysql

	private static String findMysqlZKRoot() {
		return EnvConsts.ENV_ROOT + "/" + EnvFinder.findProfile().name() + "/mysql";
	}

	public static String findMysqlMasterZKRoot() {
		return findMysqlZKRoot() + "/master";
	}

	public static String findMysqlSlaveZKRoot() {
		return findMysqlZKRoot() + "/slave";
	}

	public static DBConfig findMysqlMaster(String source) {
		return MysqlLoader.loadMaster(source);
	}

	public static DBConfig findMysqlSlave(String source) {
		return MysqlLoader.loadSlave(source);
	}

	// 1.find zk hosts
	public static String findZKHosts() {
		return EnvConsts.ZK_SERVERS;
	}

	// 2.find ssdb

	public static String findSSDBZKRoot() {
		return EnvConsts.ENV_ROOT + "/" + EnvFinder.findProfile().name() + "/ssdb";
	}

	public static SSDBX findSSDB(String source) {
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
