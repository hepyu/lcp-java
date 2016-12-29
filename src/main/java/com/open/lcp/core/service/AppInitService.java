package com.open.lcp.core.service;

import com.open.lcp.core.api.info.AppInitInfo;

public interface AppInitService {
	AppInitInfo getAppInitInfo(String deviceId);

	boolean setAppInitInfo(AppInitInfo appInitInfo);
}
