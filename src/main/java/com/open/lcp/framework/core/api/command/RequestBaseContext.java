package com.open.lcp.framework.core.api.command;

import java.util.Map;

import com.open.lcp.framework.core.api.service.dao.info.AppInfo;
import com.open.lcp.framework.security.service.UserAccountService.UserType;

/**
 * 请求相关的基础数据
 * 
 */
public class RequestBaseContext {
	private UserType userType;
	private final int apiV;
	private final long time;
	private final String httpMethod;
	private final String requestURI;
	private final String clientIp;

	private long userId;
	private Map<String, String> requestParamMap;

	private AppInfo appInfo;

	private String ticket;

	private String secretKey;

	public RequestBaseContext(int apiV, long time, String httpMethod, String reqURI, String clientIp) {
		this.apiV = apiV;
		this.time = time;
		this.httpMethod = httpMethod;
		this.requestURI = reqURI;
		this.clientIp = clientIp;
	}

	public long getUserId() {
		return userId;
	}

	public UserType getUserType() {
		return userType;
	}

	public void setUser(UserType userType, long userId) {
		this.userType = userType;
		this.userId = userId;
	}

	public int getApiV() {
		return apiV;
	}

	public long getTime() {
		return time;
	}

	public String getHttpMethod() {
		return httpMethod;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public AppInfo getAppInfo() {
		return appInfo;
	}

	public void setAppInfo(AppInfo appInfo) {
		this.appInfo = appInfo;
	}

	public String getRequestURI() {
		return requestURI;
	}

	public Map<String, String> getRequestParamMap() {
		return requestParamMap;
	}

	public void setRequestParamMap(Map<String, String> requestParamMap) {
		this.requestParamMap = requestParamMap;
	}

	public String getTicket() {
		return ticket;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;
	}

	public String getClientIp() {
		return clientIp;
	}

	public void setUserType(UserType userType) {
		this.userType = userType;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return "RequestBaseContext [userType=" + userType + ", userId=" + userId + ", time=" + time + ", httpMethod="
				+ httpMethod + ", requestURI=" + requestURI + ", requestParamMap=" + requestParamMap + ", appInfo="
				+ appInfo + ", ticket=" + ticket + ", secretKey=" + secretKey + ", clientIp=" + clientIp + "]";
	}

}
