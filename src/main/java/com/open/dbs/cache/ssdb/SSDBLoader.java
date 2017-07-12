package com.open.dbs.cache.ssdb;

import com.open.lcp.LcpResource;

public class SSDBLoader {

	public static SSDBX loadSSDBX(final LcpResource zkResourcePath) {
		return SSDBXFactory.getSSDBX(zkResourcePath);
	}

}
