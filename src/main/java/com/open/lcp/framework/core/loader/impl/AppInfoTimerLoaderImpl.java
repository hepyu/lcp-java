package com.open.lcp.framework.core.loader.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.open.lcp.framework.core.api.service.dao.AppInfoDAO;
import com.open.lcp.framework.core.api.service.dao.entity.AppAuthInfoEntity;
import com.open.lcp.framework.core.api.service.dao.entity.AppInfoEntity;
import com.open.lcp.framework.core.api.service.dao.info.AppAuthInfo;
import com.open.lcp.framework.core.api.service.dao.info.AppInfo;
import com.open.lcp.framework.core.loader.AppInfoTimerLoader;
import com.open.lcp.framework.core.loader.TimerLoader;
import com.open.lcp.framework.util.LcpUtil;

@Component
public class AppInfoTimerLoaderImpl implements TimerLoader, AppInfoTimerLoader {

	private static final Log logger = LogFactory.getLog(AppInfoTimerLoaderImpl.class);

	private Map<Integer, AppInfo> appIdAppInfoMap = null;

	private Map<Integer, List<AppAuthInfo>> appAuthMap = null;

	@Autowired
	private AppInfoDAO appInfoDAO;

	private void loadApp() {
		logger.info("loadApp start");
		long startTime = System.currentTimeMillis();
		List<AppInfoEntity> appInfos = appInfoDAO.getAppList();

		Map<Integer, AppInfo> appIdAppInfoMap = new HashMap<Integer, AppInfo>();
		Map<Integer, List<AppAuthInfo>> appAuthMap = new HashMap<Integer, List<AppAuthInfo>>();

		List<AppAuthInfoEntity> allAuths = appInfoDAO.loadAllAuthorities();
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

	@Override
	public AppInfo getAppInfo(int appId) {
		if (appId == 0 || this.appIdAppInfoMap == null) {
			return null;
		}
		return this.appIdAppInfoMap.get(appId);
	}

	@Override
	public List<AppAuthInfo> getAppAuthListByAppId(int appId) {

		if (appId == 0 || this.appIdAppInfoMap == null || appAuthMap.size() == 0) {
			return null;
		}
		List<AppAuthInfo> rtList = appAuthMap.get(appId);
		if (rtList == null) {
			return new ArrayList<AppAuthInfo>();
		} else {
			return rtList;
		}
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
			if (isClientIPAuthorized(clientIP, auth) && LcpUtil.leftMatch(methodName, auth.getAuthMethod())) {
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
			if (LcpUtil.leftMatch(clientIP, authIP)) {
				return true;
			}
		}
		return false;
	}
}
