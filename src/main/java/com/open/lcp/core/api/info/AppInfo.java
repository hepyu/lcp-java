package com.open.lcp.core.api.info;

public interface AppInfo {

	/** 接入方编号 */
	public int getAppId();

	public String getBlCode();

	/** 接入方名称 */
	public String getAppName();

	/** 接入方私钥 */
	public String getAppSecretKey();
}
