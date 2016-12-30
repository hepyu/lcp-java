package com.open.lcp.framework.core.service;

import com.open.lcp.framework.core.dao.entity.AppInitInfoEntity;

public interface AppInitService {
	AppInitInfoEntity getAppInitInfo(String deviceId);

	boolean setAppInitInfo(AppInitInfoEntity appInitInfo);
}
