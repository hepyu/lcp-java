package com.open.lcp.framework.core.loader.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.open.lcp.framework.core.api.service.dao.AppInfoDAO;
import com.open.lcp.framework.core.api.service.dao.entity.LcpApiMaxThreadsEntity;
import com.open.lcp.framework.core.loader.ApiMaxThreadsTimerLoader;

@Component
public class ApiMaxThreadsTimerLoaderImpl implements ApiMaxThreadsTimerLoader {

	private Map<String, LcpApiMaxThreadsEntity> map = new HashMap<String, LcpApiMaxThreadsEntity>(0);

	@Autowired
	private AppInfoDAO appInfoDAO;

	@Override
	public String reload() {
		List<LcpApiMaxThreadsEntity> lsApi = appInfoDAO.getApiMaxThreads();
		if (lsApi == null || lsApi.isEmpty()) {
			return "empt";
		}
		Map<String, LcpApiMaxThreadsEntity> mapApi = new HashMap<String, LcpApiMaxThreadsEntity>(lsApi.size());
		for (LcpApiMaxThreadsEntity api : lsApi) {
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
	public LcpApiMaxThreadsEntity getLcpApiMaxThreads(String api) {
		return map.get(api);
	}
}
