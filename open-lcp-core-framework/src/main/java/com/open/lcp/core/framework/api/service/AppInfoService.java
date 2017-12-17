package com.open.lcp.core.framework.api.service;

import java.util.List;

import com.open.lcp.core.api.info.BasicAppInfo;
import com.open.lcp.core.framework.api.service.dao.info.AppAuthInfo;

public interface AppInfoService {

	public BasicAppInfo getAppInfo(int appId);

	public boolean isAllowedApiMethod(int appId, String methodName, String clientIP);

	public List<AppAuthInfo> getAppAuthListByAppId(int appId);

	public int createApp(BasicAppInfo appInfo);

	public BasicAppInfo getAppInfoByAppId(int appId);

	public List<BasicAppInfo> getAppInfoList();

}
