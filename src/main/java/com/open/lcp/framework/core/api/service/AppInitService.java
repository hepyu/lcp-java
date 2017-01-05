package com.open.lcp.framework.core.api.service;

import com.open.lcp.dao.entity.AppInitInfoEntity;

public interface AppInitService {
	AppInitInfoEntity getAppInitInfo(String deviceId);

	boolean setAppInitInfo(AppInitInfoEntity appInitInfo);
}
