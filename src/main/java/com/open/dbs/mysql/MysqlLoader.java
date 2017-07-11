package com.open.dbs.mysql;

import com.open.dbs.DBConfig;
import com.open.lcp.ZKResourcePath;

public class MysqlLoader {

	public static DBConfig loadMaster(final ZKResourcePath zkResourcePath) {
		return MysqlXFactory.getMaster(zkResourcePath);
	}

	public static DBConfig loadSlave(final ZKResourcePath zkResourcePath) {
		return MysqlXFactory.getSlave(zkResourcePath);
	}
}
