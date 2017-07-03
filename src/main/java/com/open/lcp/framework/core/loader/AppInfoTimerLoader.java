package com.open.lcp.framework.core.loader;

import java.util.List;

import com.open.lcp.framework.core.api.service.dao.info.LcpAppAuthInfo;
import com.open.lcp.framework.core.api.service.dao.info.LcpAppInfo;

public interface AppInfoTimerLoader extends TimerLoader {

	public LcpAppInfo getAppInfo(int appId);

	public List<LcpAppAuthInfo> getAppAuthListByAppId(int appId);

	public boolean isAllowedApiMethod(int appId, String methodName, String clientIP);
}
