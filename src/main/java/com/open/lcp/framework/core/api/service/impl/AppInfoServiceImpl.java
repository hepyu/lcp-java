package com.open.lcp.framework.core.api.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.open.lcp.framework.core.api.service.AppInfoService;
import com.open.lcp.framework.core.api.service.dao.LcpAppInfoDAO;
import com.open.lcp.framework.core.api.service.dao.entity.LcpAppInfoEntity;
import com.open.lcp.framework.core.api.service.dao.info.LcpAppAuthInfo;
import com.open.lcp.framework.core.api.service.dao.info.LcpAppInfo;
import com.open.lcp.framework.core.loader.AppInfoTimerLoader;

@Service
public class AppInfoServiceImpl implements AppInfoService {

	@Autowired
	private LcpAppInfoDAO appInfoDAO;

	@Autowired
	private AppInfoTimerLoader appInfoTimerLoader;

	public AppInfoServiceImpl() {
	}

	@Override
	public LcpAppInfo getAppInfo(int appId) {
		return appInfoTimerLoader.getAppInfo(appId);
	}

	@Override
	public boolean isAllowedApiMethod(int appId, String methodName, String clientIP) {
		return appInfoTimerLoader.isAllowedApiMethod(appId, methodName, clientIP);
	}

	@Override
	public List<LcpAppAuthInfo> getAppAuthListByAppId(int appId) {
		return appInfoTimerLoader.getAppAuthListByAppId(appId);
	}

	@Override
	public int createApp(LcpAppInfo appInfo) {
		return appInfoDAO.createApp(appInfo);
	}

	@Override
	public LcpAppInfo getAppInfoByAppId(int appId) {
		return appInfoDAO.findAppInfoByAppId(appId);
	}

	@Override
	public List<LcpAppInfo> getAppInfoList() {
		List<LcpAppInfoEntity> list = appInfoDAO.getAppList();
		List<LcpAppInfo> rtList = new ArrayList<LcpAppInfo>();
		if (list != null) {
			for (LcpAppInfoEntity entity : list) {
				rtList.add(entity);
			}
		}
		return rtList;
	}
}
