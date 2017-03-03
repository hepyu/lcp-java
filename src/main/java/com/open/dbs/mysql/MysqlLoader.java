package com.open.dbs.mysql;

import com.open.dbs.DBConfig;

public class MysqlLoader {

	public static DBConfig loadMaster(String instanceName) {
		return MysqlXFactory.getMaster(instanceName);
	}

	public static DBConfig loadSlave(String instanceName) {
		return MysqlXFactory.getSlave(instanceName);
	}
}
