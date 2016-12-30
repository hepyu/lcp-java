package com.open.lcp.framework.core.dao.entity;

import java.io.Serializable;

import com.open.lcp.framework.core.info.AppInitInfo;

public class AppInitInfoEntity implements Serializable, AppInitInfo {

	private String deviceId;

	private String miuiVersion;

	private String productVersion;

	private String miuiType;

	private String language;

	private String country;

	private String screenHeight;

	private String device;

	private String androidVersion;

	private String model;

	private String screenResolution;

	private String screenWidth;

	private String screenDensity;

	private String mac;

	private String imei;

	private String macMd5;

	private String imeiMd5;

	private static final long serialVersionUID = 1L;

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getMiuiVersion() {
		return miuiVersion;
	}

	public void setMiuiVersion(String miuiVersion) {
		this.miuiVersion = miuiVersion;
	}

	public String getProductVersion() {
		return productVersion;
	}

	public void setProductVersion(String productVersion) {
		this.productVersion = productVersion;
	}

	public String getMiuiType() {
		return miuiType;
	}

	public void setMiuiType(String miuiType) {
		this.miuiType = miuiType;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getScreenHeight() {
		return screenHeight;
	}

	public void setScreenHeight(String screenHeight) {
		this.screenHeight = screenHeight;
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public String getAndroidVersion() {
		return androidVersion;
	}

	public void setAndroidVersion(String androidVersion) {
		this.androidVersion = androidVersion;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getScreenResolution() {
		return screenResolution;
	}

	public void setScreenResolution(String screenResolution) {
		this.screenResolution = screenResolution;
	}

	public String getScreenWidth() {
		return screenWidth;
	}

	public void setScreenWidth(String screenWidth) {
		this.screenWidth = screenWidth;
	}

	public String getScreenDensity() {
		return screenDensity;
	}

	public void setScreenDensity(String screenDensity) {
		this.screenDensity = screenDensity;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getMacMd5() {
		return macMd5;
	}

	public void setMacMd5(String macMd5) {
		this.macMd5 = macMd5;
	}

	public String getImeiMd5() {
		return imeiMd5;
	}

	public void setImeiMd5(String imeiMd5) {
		this.imeiMd5 = imeiMd5;
	}

}
