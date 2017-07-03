package com.open.lcp.framework.core.api.service;

import java.util.List;

import com.open.lcp.framework.core.api.service.dao.info.LcpAppAuthInfo;
import com.open.lcp.framework.core.api.service.dao.info.LcpAppInfo;

public interface LcpAppInfoService {

	public LcpAppInfo getAppInfo(int appId);

	public boolean isAllowedApiMethod(int appId, String methodName, String clientIP);

	public List<LcpAppAuthInfo> getAppAuthListByAppId(int appId);

	public int createApp(LcpAppInfo appInfo);

	public LcpAppInfo getAppInfoByAppId(int appId);

	public List<LcpAppInfo> getAppInfoList();

}
