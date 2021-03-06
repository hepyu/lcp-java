package com.open.lcp.core.framework.api.service;

import java.util.List;

import com.open.lcp.core.api.info.CoreFrameworkAppInfo;
import com.open.lcp.core.framework.api.service.dao.info.AppAuthInfo;

public interface AppInfoService {

	public CoreFrameworkAppInfo getAppInfo(int appId);

	public boolean isAllowedApiMethod(int appId, String methodName, String clientIP);

	public List<AppAuthInfo> getAppAuthListByAppId(int appId);

	public int createApp(CoreFrameworkAppInfo appInfo);

	public CoreFrameworkAppInfo getAppInfoByAppId(int appId);

	public List<CoreFrameworkAppInfo> getAppInfoList();

}
