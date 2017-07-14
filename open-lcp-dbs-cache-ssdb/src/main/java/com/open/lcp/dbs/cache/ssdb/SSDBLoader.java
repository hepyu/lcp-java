package com.open.lcp.dbs.cache.ssdb;

import com.open.lcp.core.model.LcpResource;

public class SSDBLoader {

	public static SSDBX loadSSDBX(final LcpResource zkResourcePath) {
		return SSDBXFactory.getSSDBX(zkResourcePath);
	}

}
