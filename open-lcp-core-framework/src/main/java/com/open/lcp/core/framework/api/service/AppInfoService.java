package com.open.lcp.core.framework.api.service;

import java.util.List;

import com.open.lcp.core.base.info.BaseAppInfo;
import com.open.lcp.core.framework.api.service.dao.info.AppAuthInfo;

public interface AppInfoService {

	public BaseAppInfo getAppInfo(int appId);

	public boolean isAllowedApiMethod(int appId, String methodName, String clientIP);

	public List<AppAuthInfo> getAppAuthListByAppId(int appId);

	public int createApp(BaseAppInfo appInfo);

	public BaseAppInfo getAppInfoByAppId(int appId);

	public List<BaseAppInfo> getAppInfoList();

}
