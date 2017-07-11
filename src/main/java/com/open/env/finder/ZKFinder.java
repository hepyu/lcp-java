package com.open.env.finder;

import org.apache.commons.lang.StringUtils;

import com.open.lcp.ZKResourcePath;

public class ZKFinder {

	// 0.find mysql

	// private static String findMysqlZKRoot() {
	// return EnvConsts.ENV_ROOT + "/" + EnvFinder.getProfile().name() +
	// "/mysql";
	// }
	//
	// public static String findMysqlMasterZKRoot() {
	// return findMysqlZKRoot() + "/master";
	// }
	//
	// public static String findMysqlSlaveZKRoot() {
	// return findMysqlZKRoot() + "/slave";
	// }
	//
	// public static DBConfig findMysqlMaster(String source) {
	// return MysqlLoader.loadMaster(source);
	// }
	//
	// public static DBConfig findMysqlSlave(String source) {
	// return MysqlLoader.loadSlave(source);
	// }

	public static String findZKHosts() {
		return EnvConsts.ZK_SERVERS;
	}

	public static String findAbsoluteZKResourcePath(ZKResourcePath zkResourcePath) {
		StringBuilder sb = new StringBuilder();
		sb.append(EnvConsts.ENV_ROOT).append("/").append(EnvFinder.getProfile().name());
		if (StringUtils.isEmpty(zkResourcePath.relativePath())) {
			sb.append("/").append(zkResourcePath.resourceName());
		} else {
			sb.append("/").append(zkResourcePath.relativePath()).append("/").append(zkResourcePath.resourceName());
		}
		return sb.toString();
	}

	public static String findZKResourceParentPath(ZKResourcePath zkResourcePath) {
		StringBuilder sb = new StringBuilder();
		sb.append(EnvConsts.ENV_ROOT).append("/").append(EnvFinder.getProfile().name());
		if (StringUtils.isEmpty(zkResourcePath.relativePath())) {
			// no doing
		} else {
			sb.append("/").append(zkResourcePath.relativePath());
		}
		return sb.toString();
	}

	// 2.find ssdb

	// public static String findSSDBZKRoot() {
	// return EnvConsts.ENV_ROOT + "/" + EnvFinder.getProfile().name() +
	// "/ssdb";
	// }
	//
	// public static SSDBX findSSDB(String source) {
	// return SSDBLoader.loadSSDBX(source);
	// }

	// 3.find redis

	// public static String findRedisZKRoot() {
	// return EnvConsts.ENV_ROOT + "/" + EnvFinder.getProfile().name() +
	// "/redis";
	// }

	// 4.find kafka

	// public static String findKafkaZKRoot() {
	// return EnvConsts.ENV_ROOT + "/" + EnvFinder.getProfile().name() +
	// "/kafka";
	// }
}
