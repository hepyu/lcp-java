package com.open.lcp.framework.core.api.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.open.lcp.framework.core.api.service.ApiInfoService;
import com.open.lcp.framework.core.api.service.AutoReloadMinutely;
import com.open.lcp.framework.core.api.service.dao.AppInfoDao;
import com.open.lcp.framework.core.api.service.dao.entity.AppAuthInfoEntity;
import com.open.lcp.framework.core.api.service.dao.entity.AppInfoEntity;
import com.open.lcp.framework.core.api.service.dao.info.AppAuthInfo;
import com.open.lcp.framework.core.api.service.dao.info.AppInfo;
import com.open.lcp.framework.util.LcpUtils;

@Service
public class ApiInfoServiceImpl implements ApiInfoService, AutoReloadMinutely {// ,
																				// InitializingBean

	private static final Log logger = LogFactory.getLog(ApiInfoServiceImpl.class);

	private Map<Integer, AppInfo> appIdAppInfoMap = null;

	private Map<Integer, List<AppAuthInfo>> appAuthMap = null;

	@Autowired
	private AppInfoDao appInfoDao;

	public ApiInfoServiceImpl() {
	}

	private void loadApp() {
		logger.info("loadApp start");
		long startTime = System.currentTimeMillis();
		List<AppInfoEntity> appInfos = appInfoDao.getAppList();

		Map<Integer, AppInfo> appIdAppInfoMap = new HashMap<Integer, AppInfo>();
		Map<Integer, List<AppAuthInfo>> appAuthMap = new HashMap<Integer, List<AppAuthInfo>>();

		List<AppAuthInfoEntity> allAuths = appInfoDao.loadAllAuthorities();
		for (AppAuthInfo auth : allAuths) {
			int appId = auth.getAppId();
			if (!appAuthMap.containsKey(appId)) {
				appAuthMap.put(appId, new ArrayList<AppAuthInfo>());
			}
			appAuthMap.get(appId).add(auth);
		}

		for (AppInfo app : appInfos) {
			appIdAppInfoMap.put(app.getAppId(), app);
		}

		this.appAuthMap = appAuthMap;
		this.appIdAppInfoMap = appIdAppInfoMap;

		logger.info("loadApp end timecost:" + (System.currentTimeMillis() - startTime));
	}

	@Override
	public AppInfo getAppInfo(int appId) {
		if (appId == 0 || this.appIdAppInfoMap == null) {
			return null;
		}
		return this.appIdAppInfoMap.get(appId);
	}

	@Override
	public boolean isAllowedApiMethod(int appId, String methodName, String clientIP) {
		if (this.appAuthMap == null) {
			return false;
		}
		List<AppAuthInfo> apiAuthList = this.appAuthMap.get(appId);
		if (apiAuthList == null) {
			return false;
		}
		for (AppAuthInfo auth : apiAuthList) {
			if (isClientIPAuthorized(clientIP, auth) && LcpUtils.leftMatch(methodName, auth.getAuthMethod())) {
				return true;
			}
		}
		return false;
	}

	private boolean isClientIPAuthorized(String clientIP, AppAuthInfo auth) {
		Set<String> authIPs = auth.getAuthIpSet();
		if (authIPs == null || authIPs.isEmpty()) {
			return true;
		}

		for (String authIP : authIPs) {
			if (LcpUtils.leftMatch(clientIP, authIP)) {
				return true;
			}
		}

		return false;
	}

	// class ConfigLoadWorker implements Runnable {
	// @Override
	// public void run() {
	// loadApp();
	// }
	//
	// }

	// @Override
	// public void afterPropertiesSet() throws Exception {
	// loadApp();
	// Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(new
	// ConfigLoadWorker(), 5, 5, TimeUnit.MINUTES);
	// }

	@Override
	public boolean initLoad() {
		return true;
	}

	@Override
	public boolean reloadable(int hour, int minute, long minuteOfAll) {
		return minute % 5 == 0;
	}

	@Override
	public String reload() {
		this.loadApp();
		return "OK";
	}

	// public AppInfoDao getAppInfoDAO() {
	// return appInfoDao;
	// }
	//
	// public void setAppInfoDAO(AppInfoDao appInfoDAO) {
	// this.appInfoDao = appInfoDAO;
	// }

	@Override
	public List<AppAuthInfo> getAppAuthListByAppId(int appId) {
		if (appId > 0 && null != appAuthMap && appAuthMap.size() > 0)
			return appAuthMap.get(appId);
		return new ArrayList<AppAuthInfo>();
	}

	@Override
	public int createApp(AppInfo appInfo) {
		return appInfoDao.createApp(appInfo);
	}

	@Override
	public AppInfo getAppInfoByAppId(int appId) {
		return appInfoDao.findAppInfoByAppId(appId);
	}

	@Override
	public List<AppInfo> getAppInfoList() {
		List<AppInfoEntity> list = appInfoDao.getAppList();
		List<AppInfo> rtList = new ArrayList<AppInfo>();
		if (list != null) {
			for (AppInfoEntity entity : list) {
				rtList.add(entity);
			}
		}
		return rtList;
	}
}
