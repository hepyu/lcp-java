package com.open.lcp.biz.app.init.service;

import com.open.lcp.biz.app.init.service.dao.entity.AppInitInfoEntity;
import com.open.lcp.core.base.service.BaseAppInitService;

public interface AppInitService extends BaseAppInitService {

	AppInitInfoEntity getAppInitInfo(String deviceId);

	boolean setAppInitInfo(AppInitInfoEntity appInitInfo);
}
