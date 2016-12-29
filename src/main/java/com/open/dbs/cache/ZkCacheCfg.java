package com.open.dbs.cache;

import java.util.List;

import com.bchbc.dbs.env.cfg.ZkCfg;

public class ZkCacheCfg extends ZkCfg {
	private List<CacheConfig> server;

	public List<CacheConfig> getServer() {
		return server;
	}

	public void setServer(List<CacheConfig> server) {
		this.server = server;
	}

}
