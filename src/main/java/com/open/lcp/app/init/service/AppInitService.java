package com.open.lcp.app.init.service;

import com.open.lcp.app.init.service.dao.entity.AppInitInfoEntity;

public interface AppInitService {
	AppInitInfoEntity getAppInitInfo(String deviceId);

	boolean setAppInitInfo(AppInitInfoEntity appInitInfo);
}
