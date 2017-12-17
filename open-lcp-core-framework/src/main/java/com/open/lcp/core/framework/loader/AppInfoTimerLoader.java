package com.open.lcp.core.framework.loader;

import java.util.List;

import com.open.lcp.core.api.info.BaseAppInfo;
import com.open.lcp.core.framework.api.service.dao.info.AppAuthInfo;

public interface AppInfoTimerLoader extends TimerLoader {

	public BaseAppInfo getAppInfo(int appId);

	public List<AppAuthInfo> getAppAuthListByAppId(int appId);

	public boolean isAllowedApiMethod(int appId, String methodName, String clientIP);
}
