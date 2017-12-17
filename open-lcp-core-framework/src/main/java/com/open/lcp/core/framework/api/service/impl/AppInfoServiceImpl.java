package com.open.lcp.core.framework.api.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.open.lcp.core.api.info.BaseAppInfo;
import com.open.lcp.core.framework.api.service.AppInfoService;
import com.open.lcp.core.framework.api.service.dao.AppInfoDAO;
import com.open.lcp.core.framework.api.service.dao.entity.AppInfoEntity;
import com.open.lcp.core.framework.api.service.dao.info.AppAuthInfo;
import com.open.lcp.core.framework.loader.AppInfoTimerLoader;

@Service
public class AppInfoServiceImpl implements AppInfoService {

	@Autowired
	private AppInfoDAO appInfoDAO;

	@Autowired
	private AppInfoTimerLoader appInfoTimerLoader;

	public AppInfoServiceImpl() {
	}

	@Override
	public BaseAppInfo getAppInfo(int appId) {
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
	public int createApp(BaseAppInfo appInfo) {
		return appInfoDAO.createApp(appInfo);
	}

	@Override
	public BaseAppInfo getAppInfoByAppId(int appId) {
		return appInfoDAO.findAppInfoByAppId(appId);
	}

	@Override
	public List<BaseAppInfo> getAppInfoList() {
		List<AppInfoEntity> list = appInfoDAO.getAppList();
		List<BaseAppInfo> rtList = new ArrayList<BaseAppInfo>();
		if (list != null) {
			for (AppInfoEntity entity : list) {
				rtList.add(entity);
			}
		}
		return rtList;
	}
}
