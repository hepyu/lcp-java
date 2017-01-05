package com.open.lcp.framework.core.api.service;

import java.util.List;

import com.open.lcp.dao.info.AppAuthInfo;
import com.open.lcp.dao.info.AppInfo;

public interface AppInfoService {

	public AppInfo getAppInfo(int appId);

	public boolean isAllowedApiMethod(int appId, String methodName, String clientIP);

	public List<AppAuthInfo> getAppAuthListByAppId(int appId);

	public int createApp(AppInfo appInfo);

	public AppInfo getAppInfoByAppId(int appId);

	public List<AppInfo> getAppInfoList();

}
