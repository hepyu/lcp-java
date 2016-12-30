package com.open.lcp.framework.core.dao.entity;

public class ApiMaxThreadsEntity {

	private String api;
	private int maxThreads;
	private String outResp;
	private String keysReq;

	private transient String[] keys;

	public String getApi() {
		return api;
	}

	public void setApi(String api) {
		this.api = api;
	}

	public int getMaxThreads() {
		return maxThreads;
	}

	public void setMaxThreads(int maxThreads) {
		this.maxThreads = maxThreads;
	}

	public String getOutResp() {
		return outResp;
	}

	public void setOutResp(String outResp) {
		this.outResp = outResp;
	}

	public String getKeysReq() {
		return keysReq;
	}

	public void setKeysReq(String keysReq) {
		this.keysReq = keysReq;
	}

	public String[] getKeys() {
		return keys;
	}

	public void setKeys(String[] keys) {
		this.keys = keys;
	}

}
