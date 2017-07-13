package com.open.lcp.core.framework.api.service;

import java.util.List;
import java.util.Map;

import com.open.lcp.core.framework.api.command.ApiCommand;
import com.open.lcp.core.framework.api.command.ApiFacadeMethod;

public interface ApiCommandLookupService {

	/**
	 * lookup api command from methodValue
	 * 
	 * @param methodValue
	 * @return
	 */
	public ApiCommand lookupApiCommand(ApiFacadeMethod apiFacadeMethod);

	/**
	 * 接口是否免授权，直接开放。
	 * 
	 * @param methodValue
	 * @param version
	 * @return
	 */
	public boolean isOpen(String methodValue, String version);

	/**
	 * 是否需要登录
	 * 
	 * @param methodvalue
	 * @return
	 */
	public boolean isNeedLogin(String methodValue, String version);

	/**
	 * 取所有的接口名称列表
	 * 
	 * @return
	 */
	public Map<String, List<String>> getCommands();

	/**
	 * 取当前支持的方法及版本列表
	 * 
	 * @return
	 */
	String[] getApiAndVerList();

	/**
	 * 取当前版本方法的调用次数合计
	 * 
	 * @param apiAndVer
	 * @return
	 */
	long getApiAndVerPv(String apiAndVer);

}
