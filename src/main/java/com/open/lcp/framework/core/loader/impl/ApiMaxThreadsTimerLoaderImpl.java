package com.open.lcp.framework.core.loader.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.open.lcp.dao.AppInfoDAO;
import com.open.lcp.dao.entity.ApiMaxThreadsEntity;
import com.open.lcp.framework.core.loader.ApiMaxThreadsTimerLoader;

@Component
public class ApiMaxThreadsTimerLoaderImpl implements ApiMaxThreadsTimerLoader {

	private Map<String, ApiMaxThreadsEntity> map = new HashMap<String, ApiMaxThreadsEntity>(0);

	@Autowired
	private AppInfoDAO appInfoDAO;

	@Override
	public String reload() {
		List<ApiMaxThreadsEntity> lsApi = appInfoDAO.getApiMaxThreads();
		if (lsApi == null || lsApi.isEmpty()) {
			return "empt";
		}
		Map<String, ApiMaxThreadsEntity> mapApi = new HashMap<String, ApiMaxThreadsEntity>(lsApi.size());
		for (ApiMaxThreadsEntity api : lsApi) {
			api.setOutResp(api.getOutResp().trim());
			final String keys = api.getKeysReq().trim();
			if (keys.length() > 0) {
				api.setKeys(keys.split("[,;]"));
			}
			mapApi.put(api.getApi(), api);
		}
		this.map = mapApi;
		return "OK";
	}

	@Override
	public boolean initLoad() {
		return true;
	}

	@Override
	public boolean reloadable(int hour, int minute, long minuteOfAll) {
		return minute % 5 == 3;
	}

	@Override
	public ApiMaxThreadsEntity getLcpApiMaxThreads(String api) {
		return map.get(api);
	}
}
