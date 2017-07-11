package com.open.dbs.cache.ssdb;

import com.open.lcp.ZKResourcePath;

public class SSDBLoader {

	public static SSDBX loadSSDBX(final ZKResourcePath zkResourcePath) {
		return SSDBXFactory.getSSDBX(zkResourcePath);
	}

}
