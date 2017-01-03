package com.open.lcp.framework.core.api.service.impl;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.open.dbs.cache.SSDBX;
import com.open.env.finder.EnvFinder;
import com.open.lcp.framework.core.api.service.AppInitService;
import com.open.lcp.framework.core.api.service.dao.AppInitInfoDao;
import com.open.lcp.framework.core.api.service.dao.entity.AppInitInfoEntity;

@Service
public class AppInitServiceImpl implements AppInitService {

	private static final Log logger = LogFactory.getLog(AppInitServiceImpl.class);
	private static final String APP_INIT_KEY = "app-init-";

	private SSDBX ssdbx = EnvFinder.loadSSDBX("lcp_app_init");

	@Resource
	AppInitInfoDao appInitDao;

	@Override
	public AppInitInfoEntity getAppInitInfo(String deviceId) {
		try {
			AppInitInfoEntity appInitInfo = ssdbx.get(APP_INIT_KEY + deviceId, AppInitInfoEntity.class);
			if (appInitInfo == null) {
				appInitInfo = appInitDao.getAppInit(deviceId);
				if (appInitInfo == null) {
					ssdbx.set(APP_INIT_KEY + deviceId, "{}");
					return null;
				}
				ssdbx.set(APP_INIT_KEY + deviceId, appInitInfo);
				return appInitInfo;
			} else {
				if (StringUtils.isBlank(appInitInfo.getDeviceId())) {
					return null;
				}
				return appInitInfo;
			}
		} catch (Exception e) {
			logger.error("init ssdb query error deviceId:" + deviceId, e);
		}
		return appInitDao.getAppInit(deviceId);
	}

	@Override
	public boolean setAppInitInfo(AppInitInfoEntity appInitInfo) {

		AppInitInfoEntity app = (AppInitInfoEntity) appInitInfo;
		if (logger.isDebugEnabled()) {
			logger.debug("app.init ------- " + app);
		}
		int result = appInitDao.saveAppInit(app);

		if (result > 0 && ssdbx.set(APP_INIT_KEY + app.getDeviceId(), app) > -1) {
			return true;
		}
		return false;
	}

}
