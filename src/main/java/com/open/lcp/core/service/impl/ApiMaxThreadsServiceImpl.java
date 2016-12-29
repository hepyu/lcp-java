package com.open.lcp.core.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.open.lcp.core.api.entity.ApiMaxThreads;
import com.open.lcp.core.dao.AppInfoDao;
import com.open.lcp.core.service.ApiMaxThreadsService;
import com.open.lcp.core.service.AutoReloadMinutely;

@Service
public class ApiMaxThreadsServiceImpl implements AutoReloadMinutely, ApiMaxThreadsService {
	@Autowired
	private AppInfoDao appInfoDAO;

	private Map<String, ApiMaxThreads> map = new HashMap<String, ApiMaxThreads>(0);

	@Override
	public ApiMaxThreads getMcpApiMaxThreads(String api) {
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
		List<ApiMaxThreads> lsApi = appInfoDAO.getApiMaxThreads();
		if (lsApi == null || lsApi.isEmpty()) {
			return "empt";
		}
		Map<String, ApiMaxThreads> mapApi = new HashMap<String, ApiMaxThreads>(lsApi.size());
		for (ApiMaxThreads api : lsApi) {
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
