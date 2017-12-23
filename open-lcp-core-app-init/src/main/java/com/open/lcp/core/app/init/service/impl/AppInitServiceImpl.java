package com.open.lcp.core.app.init.service.impl;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.open.lcp.core.app.init.service.AppInitService;
import com.open.lcp.core.app.init.service.dao.AppInitInfoDAO;
import com.open.lcp.core.app.init.service.dao.entity.AppInitInfoEntity;
import com.open.lcp.core.env.LcpResource;
import com.open.lcp.dbs.cache.redis.RedisX;
import com.open.lcp.dbs.cache.redis.RedisXFactory;

@Service
public class AppInitServiceImpl implements AppInitService {

	private static final Log logger = LogFactory.getLog(AppInitServiceImpl.class);
	private static final String APP_INIT_KEY = "app-init-";

	private RedisX redis = RedisXFactory.loadRedisX(LcpResource.lcp_redis_biz);

	@Resource
	AppInitInfoDAO appInitDao;

	@Override
	public AppInitInfoEntity getAppInitInfo(String deviceId) {
		try {
			AppInitInfoEntity appInitInfo = redis.get(APP_INIT_KEY + deviceId, AppInitInfoEntity.class);
			if (appInitInfo == null) {
				appInitInfo = appInitDao.getAppInit(deviceId);
				if (appInitInfo == null) {
					redis.set(APP_INIT_KEY + deviceId, "{}");
					return null;
				}
				redis.set(APP_INIT_KEY + deviceId, appInitInfo);
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

		if (result > 0 && redis.set(APP_INIT_KEY + app.getDeviceId(), app) > -1) {
			return true;
		}
		return false;
	}

}
