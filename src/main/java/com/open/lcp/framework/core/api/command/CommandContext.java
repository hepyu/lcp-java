package com.open.lcp.framework.core.api.command;

import java.util.Map;
import com.open.lcp.framework.core.api.service.dao.info.AppInfo;
import com.open.lcp.framework.core.api.service.dao.info.AppInitInfo;
import com.open.lcp.passport.dto.PassportUserAccountDTO;

/**
 * 接口执行时的上下文信息
 * 
 * @author hepengyuan
 */
public interface CommandContext {

	public long getBeginTime();

	public String getMethodName();

	/** 登录的userId */
	public long getUserId();

	public PassportUserAccountDTO getUserInfo();

	public AppInfo getAppInfo();

	public String getDeviceId();

	public String getSecretKey();

	public String getTicket();

	public byte[] getAesKey();

	public String getAB();

	public String getV();

	public void addStatExt(String extKey, Object extValue);

	public Map<String, String> getStringParams();

	/**
	 * 上传的文件列表，里面的Value对象类型是 org.springframework.web.multipart.MultipartFile
	 * 
	 * @return
	 */
	public Map<String, Object> getBinaryParams();

	public int getIntParamValue(String paramName);

	public int getIntParamValue(String paramName, int defValue);

	public long getLongParamValue(String paramName);

	public long getLongParamValue(String paramName, long defValue);

	public String getParamValue(String paramName);

	public String getSig();

	public String getXForwardedFor();

	public String getClientIp();

	public int getClientPort();

	public String getUserAgent();

	public String getLanguage();

	public String getHttpHead(String name);

	/** 仅当McpMethod注解中的loadAppInitData=true时，才加载 */
	public AppInitInfo getAppInitInfo();
}
