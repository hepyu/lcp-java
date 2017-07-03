package com.open.lcp.framework.core.api.service.dao.info;

public interface LcpAppInfo {

	public int getAppId();

	public int getAppPlatformId();

	public int getAppOsId();

	public String getAppName();

	public String getAppSecretKey();

	public String getAppPackageName();

	public String getAddTime();

	public String getBlCode();

	public String getResponsible();

	public int getIsPoint();

	public int getIsShortMsg();

	public String getRecommendPlatform();

}
