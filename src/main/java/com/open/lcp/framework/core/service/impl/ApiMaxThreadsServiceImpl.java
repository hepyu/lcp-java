package com.open.lcp.framework.core.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.open.lcp.framework.core.dao.AppInfoDao;
import com.open.lcp.framework.core.dao.entity.ApiMaxThreadsEntity;
import com.open.lcp.framework.core.service.ApiMaxThreadsService;
import com.open.lcp.framework.core.service.AutoReloadMinutely;

@Service
public class ApiMaxThreadsServiceImpl implements AutoReloadMinutely, ApiMaxThreadsService {
	@Autowired
	private AppInfoDao appInfoDAO;

	private Map<String, ApiMaxThreadsEntity> map = new HashMap<String, ApiMaxThreadsEntity>(0);

	@Override
	public ApiMaxThreadsEntity getMcpApiMaxThreads(String api) {
		return map.get(api);
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

}
