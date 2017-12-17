package com.open.lcp.core.app.init.service;

import com.open.lcp.core.api.service.BaseAppInitService;
import com.open.lcp.core.app.init.service.dao.entity.AppInitInfoEntity;

public interface AppInitService extends BaseAppInitService {

	AppInitInfoEntity getAppInitInfo(String deviceId);

	boolean setAppInitInfo(AppInitInfoEntity appInitInfo);
}
