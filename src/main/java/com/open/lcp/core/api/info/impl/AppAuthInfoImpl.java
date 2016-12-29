package com.open.lcp.core.api.info.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.util.StringUtils;

public class AppAuthInfoImpl {
	private int id, appId;
	private String authMethod, authIps, addTime;
	private Set<String> authIpSet;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAppId() {
		return appId;
	}

	public void setAppId(int appId) {
		this.appId = appId;
	}

	public String getAuthMethod() {
		return authMethod;
	}

	public void setAuthMethod(String authMethod) {
		this.authMethod = authMethod;
	}

	public String getAuthIps() {
		return authIps;
	}

	public void setAuthIps(String authIps) {
		this.authIps = authIps;
		if (StringUtils.isEmpty(authIps)) {
			this.authIpSet = Collections.emptySet();
			return;
		}

		this.authIpSet = new HashSet<String>();
		String[] ips = authIps.split(",");
		for (String ip : ips) {
			this.authIpSet.add(ip.trim());
		}
	}

	public String getAddTime() {
		return addTime;
	}

	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}

	public Set<String> getAuthIpSet() {
		return authIpSet;
	}

}
