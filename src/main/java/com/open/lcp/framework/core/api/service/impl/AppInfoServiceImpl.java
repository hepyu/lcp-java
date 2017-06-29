package com.open.lcp.framework.core.api.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.open.lcp.framework.core.api.service.AppInfoService;
import com.open.lcp.framework.core.api.service.dao.AppInfoDAO;
import com.open.lcp.framework.core.api.service.dao.entity.AppInfoEntity;
import com.open.lcp.framework.core.api.service.dao.info.AppAuthInfo;
import com.open.lcp.framework.core.api.service.dao.info.AppInfo;
import com.open.lcp.framework.core.loader.AppInfoTimerLoader;
import com.open.lcp.passport.service.AccountInfoService;
import com.open.lcp.passport.service.dao.PassportOAuthAccountDAO;
import com.open.lcp.passport.service.dao.PassportUserAccountDAO;

@Service
public class AppInfoServiceImpl implements AppInfoService {

	@Autowired
	private AppInfoDAO appInfoDAO;

	@Autowired
	private AppInfoTimerLoader appInfoTimerLoader;

	public AppInfoServiceImpl() {
	}

	@Override
	public AppInfo getAppInfo(int appId) {
		return appInfoTimerLoader.getAppInfo(appId);
	}

	@Override
	public boolean isAllowedApiMethod(int appId, String methodName, String clientIP) {
		return appInfoTimerLoader.isAllowedApiMethod(appId, methodName, clientIP);
	}

	@Override
	public List<AppAuthInfo> getAppAuthListByAppId(int appId) {
		return appInfoTimerLoader.getAppAuthListByAppId(appId);
	}

	@Override
	public int createApp(AppInfo appInfo) {
		return appInfoDAO.createApp(appInfo);
	}

	@Override
	public AppInfo getAppInfoByAppId(int appId) {
		return appInfoDAO.findAppInfoByAppId(appId);
	}

	@Override
	public List<AppInfo> getAppInfoList() {
		List<AppInfoEntity> list = appInfoDAO.getAppList();
		List<AppInfo> rtList = new ArrayList<AppInfo>();
		if (list != null) {
			for (AppInfoEntity entity : list) {
				rtList.add(entity);
			}
		}
		return rtList;
	}
}
