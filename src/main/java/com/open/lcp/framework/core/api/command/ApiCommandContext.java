package com.open.lcp.framework.core.api.command;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.math.NumberUtils;
import com.open.common.enums.UserType;
import com.open.lcp.app.init.service.dao.info.AppInitInfo;
import com.open.lcp.framework.core.api.service.dao.info.AppInfo;
import com.open.lcp.framework.core.consts.LcpConstants;
import com.open.lcp.passport.dto.PassportUserAccountDTO;
import com.open.lcp.passport.service.AccountInfoService;

public class ApiCommandContext implements CommandContext {
	private static final String KEY_EXT_STAT_USERID = "userId";
	private static final String KEY_EXT_STAT_USERTYPE = "userType";
	// private static final Log logger =
	// LogFactory.getLog(ApiCommandContext.class);
	// private final AccountApi accountApi;

	private AccountInfoService accountInfoService;

	private long beginTime;

	/** 当前调用的接口名 */
	private String methodName;

	/** 当前的用户编号 */
	private long userId;
	private UserType userType;
	/** 客户端接入授权信息 */
	private AppInfo appInfo;

	/** 设备唯一标识 */
	private String deviceId;

	/** 当前的安全密钥 */
	private String secretKey;

	/** 当前用户的票 */
	private String ticket;

	/** 当前应用的版本号 */
	private String appVersion;

	/** 灰度测试的值 */
	private String ab;
	/** 接口的版本号 */
	private String v;

	/** 当前请求的检查标识 */
	private final String sig;

	/** 普通参数 */
	private Map<String, String> stringParams;

	/** 二进制文件 */
	private Map<String, Object> binaryParams;

	/** 请求的Http协议头 */
	private final Map<String, String> reqHeads;
	private Map<String, Object> statExtMap;

	/** 用户的IP */
	private final String clientIp;
	/** 用户的端口 */
	private int clientPort;
	private AppInitInfo appInitInfo = null;

	private byte[] aesKey;
	private byte[] octetBody;

	public ApiCommandContext(long beginTime, AppInfo appInfo, Map<String, String> stringParams,
			Map<String, Object> binaryParams, String ticket, String secretKey, String methodName,
			Map<String, String> reqHeads, String clientIp, int clientPort, AccountInfoService userAccountService) {
		this.beginTime = beginTime;
		this.appInfo = appInfo;
		this.reqHeads = reqHeads;
		// this.userId = userId;
		this.stringParams = stringParams;
		this.binaryParams = binaryParams;
		this.ticket = ticket;
		this.secretKey = secretKey;
		this.methodName = methodName;
		this.clientIp = clientIp;
		this.clientPort = clientPort;
		this.deviceId = stringParams.get(LcpConstants.PARAM_DEVICEID);
		if (this.deviceId == null || this.deviceId.isEmpty()) {
			this.deviceId = stringParams.get(LcpConstants.PARAM_GUID);
		}
		this.sig = stringParams.get(LcpConstants.PARAM_SIG);
		this.v = stringParams.get(LcpConstants.PARAM_V);
		this.appVersion = stringParams.get(LcpConstants.PARAM_APP_VERSION);
		this.aesKey = DigestUtils.md5(this.appInfo.getAppSecretKey());
		// this.accountApi = accountApi;
		this.accountInfoService = userAccountService;
	}

	public String getAppVersion() {
		return appVersion;
	}

	public byte[] getOctetBody() {
		return octetBody;
	}

	public void setOctetBody(byte[] octetBody) {
		this.octetBody = octetBody;
	}

	@Override
	public long getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(long beginTime) {
		this.beginTime = beginTime;
	}

	@Override
	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	@Override
	public long getUserId() {
		return userId;
	}

	public UserType getUserType() {
		return userType;
	}

	/**
	 * 设置此userId时，会同时设置userIdMaybe
	 * 
	 * @param userId
	 */
	public void setUser(UserType userType, long userId) {
		this.userType = userType;
		this.userId = userId;
		if (this.userId > 0) {
			this.addStatExt(KEY_EXT_STAT_USERID, this.userId);
			this.addStatExt(KEY_EXT_STAT_USERTYPE, userType.name());
		}
	}

	@Override
	public AppInfo getAppInfo() {
		return appInfo;
	}

	public void setAppInfo(AppInfo appInfo) {
		this.appInfo = appInfo;
	}

	public String getDeviceId() {
		return deviceId;
	}

	@Override
	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	@Override
	public String getTicket() {
		return ticket;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;
	}

	@Override
	public Map<String, String> getStringParams() {
		return stringParams;
	}

	public void setStringParams(Map<String, String> stringParams) {
		this.stringParams = stringParams;
	}

	@Override
	public Map<String, Object> getBinaryParams() {
		return binaryParams;
	}

	public void setBinaryParams(Map<String, Object> binaryParams) {
		this.binaryParams = binaryParams;
	}

	@Override
	public int getIntParamValue(String paramName) {
		return this.getIntParamValue(paramName, 0);
	}

	@Override
	public int getIntParamValue(String paramName, int defValue) {
		return NumberUtils.toInt(stringParams.get(paramName), defValue);
	}

	@Override
	public long getLongParamValue(String paramName) {
		return this.getLongParamValue(paramName, 0);
	}

	@Override
	public long getLongParamValue(String paramName, long defValue) {
		return NumberUtils.toLong(stringParams.get(paramName), defValue);
	}

	@Override
	public String getParamValue(String paramName) {
		return stringParams.get(paramName);
	}

	@Override
	public String getSig() {
		return sig;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mid.mcp.model.CommandContext#getXForwardedFor()
	 */
	@Override
	public String getXForwardedFor() {
		return this.getHttpHead("X-Forwarded-For");
	}

	@Override
	public String toString() {
		return "ApiCommandContext [beginTime=" + beginTime + ", methodName=" + methodName + ", userId=" + userId
				+ ", appInfo=" + appInfo + ", deviceId=" + deviceId + ", secretKey=" + secretKey + ", ticket=" + ticket
				+ ", sig=" + sig + ", stringParams=" + stringParams + ", binaryParams=" + binaryParams + "]";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mid.mcp.model.CommandContext#getClientIp()
	 */
	@Override
	public String getClientIp() {
		return this.clientIp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mid.mcp.model.CommandContext#getUserAgent()
	 */
	@Override
	public String getUserAgent() {
		return this.getHttpHead("User-Agent");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mid.mcp.model.CommandContext#getLanguage()
	 */
	@Override
	public String getLanguage() {
		return this.getHttpHead("Accept-Language");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mid.mcp.model.CommandContext#getHttpHead(java.lang.String)
	 */
	@Override
	public String getHttpHead(String name) {
		if (this.reqHeads == null)
			return null;
		return this.reqHeads.get(name);
	}

	public Map<String, String> getHttpHeads() {
		return this.reqHeads;
	}

	@Override
	public AppInitInfo getAppInitInfo() {
		return appInitInfo;
	}

	public void setAppInitInfo(AppInitInfo appInitInfo) {
		this.appInitInfo = appInitInfo;
	}

	public String getAB() {
		if (this.ab == null) {
			if (this.getDeviceId() == null || this.getDeviceId().isEmpty()) {
				this.ab = "";
			} else {
				String md5DeviceId = DigestUtils.md5Hex(this.getDeviceId().getBytes()).toLowerCase();
				this.ab = md5DeviceId.substring(md5DeviceId.length() - 1);
			}
			this.addStatExt("ab", this.ab);
		}
		return this.ab;
	}

	public void addStatExt(String extKey, Object extValue) {
		if (this.statExtMap == null) {
			this.statExtMap = new HashMap<String, Object>();
		}
		this.statExtMap.put(extKey, extValue);
	}

	public Map<String, Object> getStatExt() {
		return this.statExtMap;
	}

	@Override
	public byte[] getAesKey() {
		return this.aesKey;
	}

	@Override
	public String getV() {
		return this.v;
	}

	private PassportUserAccountDTO userInfo = null;

	@Override
	public PassportUserAccountDTO getUserInfo() {
		if (this.userId <= 0) {
			return null;
		}
		if (userInfo != null) {
			return userInfo;
		}
		this.userInfo = accountInfoService.getUserInfo(this.userId);// accountApi.getUserInfoByXlUserId(this.userId);
		return this.userInfo;
	}

	@Override
	public int getClientPort() {
		return this.clientPort;
	}

}
