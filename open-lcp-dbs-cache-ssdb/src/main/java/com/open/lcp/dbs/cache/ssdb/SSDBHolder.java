package com.open.lcp.dbs.cache.ssdb;

import java.io.IOException;

import org.nutz.ssdb4j.SSDBs;
import org.nutz.ssdb4j.impl.SimpleClient;
import org.nutz.ssdb4j.spi.SSDB;

import com.open.lcp.core.common.JsonObjectConv;

class SSDBHolder {
	private final JsonObjectConv jsonConv;

	private SSDB ssdb;

	public SSDB getSsdb() {
		return ssdb;
	}

	public SSDBHolder(ZKSSDBConfig cfg, JsonObjectConv jsonConv) {
		this.setSSDBConfig(cfg);
		this.jsonConv = jsonConv;
	}

	public void setSSDBConfig(ZKSSDBConfig cfg) {
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
