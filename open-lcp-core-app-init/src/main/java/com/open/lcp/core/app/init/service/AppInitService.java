package com.open.lcp.core.app.init.service;

import com.open.lcp.core.api.service.CoreFeatureAppInitService;
import com.open.lcp.core.app.init.service.dao.entity.AppInitInfoEntity;

public interface AppInitService extends CoreFeatureAppInitService {

	AppInitInfoEntity getAppInitInfo(String deviceId);

	boolean setAppInitInfo(AppInitInfoEntity appInitInfo);
}
