package com.open.dbs.cache.ssdb;

import java.io.IOException;

import org.nutz.ssdb4j.SSDBs;
import org.nutz.ssdb4j.impl.SimpleClient;
import org.nutz.ssdb4j.spi.SSDB;

class SSDBHolder {
	private final JsonObjectConv jsonConv;

	private SSDB ssdb;

	public SSDB getSsdb() {
		return ssdb;
	}

	public SSDBHolder(CacheConfig cfg, JsonObjectConv jsonConv) {
		this.setSsdb(cfg);
		this.jsonConv = jsonConv;
	}

	public void setSsdb(CacheConfig cfg) {
		SSDB old = this.ssdb;
		final byte[] auth = (cfg.getAuth() == null || cfg.getAuth().isEmpty()) ? null : (cfg.getAuth().getBytes());
		this.ssdb = SSDBs.pool(cfg.getIp(), cfg.getPort(), cfg.getTimeout(), cfg.getCfg(), auth);
		if (SimpleClient.class.isInstance(ssdb)) {
			SimpleClient sc = (SimpleClient) ssdb;
			sc.setObjectConv(jsonConv);
		}
		if (old != null) {
			try {
				old.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
