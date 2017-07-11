package com.open.lcp.plugin.appinit.service;

import com.open.lcp.plugin.appinit.service.dao.entity.AppInitInfoEntity;

public interface AppInitService {
	AppInitInfoEntity getAppInitInfo(String deviceId);

	boolean setAppInitInfo(AppInitInfoEntity appInitInfo);
}
