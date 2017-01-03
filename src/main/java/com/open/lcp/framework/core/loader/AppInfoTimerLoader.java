package com.open.lcp.framework.core.loader;

import java.util.List;

import com.open.lcp.framework.core.api.service.dao.info.AppAuthInfo;
import com.open.lcp.framework.core.api.service.dao.info.AppInfo;

public interface AppInfoTimerLoader extends TimerLoader {

	public AppInfo getAppInfo(int appId);

	public List<AppAuthInfo> getAppAuthListByAppId(int appId);

	public boolean isAllowedApiMethod(int appId, String methodName, String clientIP);
}
