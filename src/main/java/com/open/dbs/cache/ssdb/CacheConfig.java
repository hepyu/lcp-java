package com.open.dbs.cache.ssdb;

import org.apache.commons.pool.impl.GenericObjectPool.Config;

public class CacheConfig {
	private String ip, auth;
	private int port;
	private int timeout;
	private Config cfg;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getAuth() {
		return auth;
	}

	public void setAuth(String auth) {
		this.auth = auth;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public Config getCfg() {
		return cfg;
	}

	public void setCfg(Config cfg) {
		this.cfg = cfg;
	}

}
