package com.open.lcp.core.framework.api.service.dao.entity;

import java.io.Serializable;

import com.open.lcp.core.api.info.BasicAppInfo;

public class AppInfoEntity implements Serializable, BasicAppInfo {

	private static final long serialVersionUID = -1119697844347595668L;
	public static final int IS_0 = 0;
	public static final int IS_1 = 1;
	private int appId, appPlatformId, appOsId, isPoint, isShortMsg, isUseHttps;
	private String appName, appSecretKey, appPackageName, addTime, blCode, responsible, recommendPlatform;

	public int getAppId() {
		return appId;
	}

	public void setAppId(int appId) {
		this.appId = appId;
	}

	public int getAppPlatformId() {
		return appPlatformId;
	}

	public void setAppPlatformId(int appPlatformId) {
		this.appPlatformId = appPlatformId;
	}

	public int getAppOsId() {
		return appOsId;
	}

	public void setAppOsId(int appOsId) {
		this.appOsId = appOsId;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getAppSecretKey() {
		return appSecretKey;
	}

	public void setAppSecretKey(String appSecretKey) {
		this.appSecretKey = appSecretKey;
	}

	public String getAppPackageName() {
		return appPackageName;
	}

	public void setAppPackageName(String appPackageName) {
		this.appPackageName = appPackageName;
	}

	public String getAddTime() {
		return addTime;
	}

	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}

	public String getBlCode() {
		return blCode;
	}

	public void setBlCode(String blCode) {
		this.blCode = blCode;
	}

	public String getResponsible() {
		return responsible;
	}

	public void setResponsible(String responsible) {
		this.responsible = responsible;
	}

	public int getIsPoint() {
		return isPoint;
	}

	public void setIsPoint(int isPoint) {
		this.isPoint = isPoint;
	}

	public int getIsShortMsg() {
		return isShortMsg;
	}

	public void setIsShortMsg(int isShortMsg) {
		this.isShortMsg = isShortMsg;
	}

	public String getRecommendPlatform() {
		return recommendPlatform;
	}

	public void setRecommendPlatform(String recommendPlatform) {
		this.recommendPlatform = recommendPlatform;
	}

	public int getIsUseHttps() {
		return isUseHttps;
	}

	public void setIsUseHttps(int isUseHttps) {
		this.isUseHttps = isUseHttps;
	}

}
