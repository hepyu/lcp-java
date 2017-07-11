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

import com.open.lcp.framework.core.api.service.dao.LcpAppInfoDAO;
import com.open.lcp.framework.core.api.service.dao.entity.LcpAppAuthInfoEntity;
import com.open.lcp.framework.core.api.service.dao.entity.LcpAppInfoEntity;
import com.open.lcp.framework.core.api.service.dao.info.LcpAppAuthInfo;
import com.open.lcp.framework.core.api.service.dao.info.LcpAppInfo;
import com.open.lcp.framework.core.loader.AppInfoTimerLoader;
import com.open.lcp.framework.core.loader.TimerLoader;
import com.open.lcp.framework.util.LcpUtil;

@Component
public class AppInfoTimerLoaderImpl implements TimerLoader, AppInfoTimerLoader {

	private static final Log logger = LogFactory.getLog(AppInfoTimerLoaderImpl.class);

	private Map<Integer, LcpAppInfo> appIdAppInfoMap = null;

	private Map<Integer, List<LcpAppAuthInfo>> appAuthMap = null;

	@Autowired
	private LcpAppInfoDAO appInfoDAO;

	private void loadApp() {
		logger.info("loadApp start");
		long startTime = System.currentTimeMillis();
		List<LcpAppInfoEntity> appInfos = appInfoDAO.getAppList();

		Map<Integer, LcpAppInfo> appIdAppInfoMap = new HashMap<Integer, LcpAppInfo>();
		Map<Integer, List<LcpAppAuthInfo>> appAuthMap = new HashMap<Integer, List<LcpAppAuthInfo>>();

		List<LcpAppAuthInfoEntity> allAuths = appInfoDAO.loadAllAuthorities();
		for (LcpAppAuthInfo auth : allAuths) {
			int appId = auth.getAppId();
			if (!appAuthMap.containsKey(appId)) {
				appAuthMap.put(appId, new ArrayList<LcpAppAuthInfo>());
			}
			appAuthMap.get(appId).add(auth);
		}

		for (LcpAppInfo app : appInfos) {
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
	public LcpAppInfo getAppInfo(int appId) {
		if (appId == 0 || this.appIdAppInfoMap == null) {
			return null;
		}
		return this.appIdAppInfoMap.get(appId);
	}

	@Override
	public List<LcpAppAuthInfo> getAppAuthListByAppId(int appId) {

		if (appId == 0 || this.appIdAppInfoMap == null || appAuthMap.size() == 0) {
			return null;
		}
		List<LcpAppAuthInfo> rtList = appAuthMap.get(appId);
		if (rtList == null) {
			return new ArrayList<LcpAppAuthInfo>();
		} else {
			return rtList;
		}
	}

	@Override
	public boolean isAllowedApiMethod(int appId, String methodName, String clientIP) {
		if (this.appAuthMap == null) {
			return false;
		}
		List<LcpAppAuthInfo> apiAuthList = this.appAuthMap.get(appId);
		if (apiAuthList == null) {
			return false;
		}
		for (LcpAppAuthInfo auth : apiAuthList) {
			if (isClientIPAuthorized(clientIP, auth) && LcpUtil.leftMatch(methodName, auth.getAuthMethod())) {
				return true;
			}
		}
		return false;

	}

	private boolean isClientIPAuthorized(String clientIP, LcpAppAuthInfo auth) {
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
