package com.open.lcp.core.api.info;

import java.util.Set;

public interface AppAuthInfo {
	public int getId();

	public int getAppId();

	public String getAuthMethod();

	public String getAuthIps();

	public String getAddTime();

	public Set<String> getAuthIpSet();
}
