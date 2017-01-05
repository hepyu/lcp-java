package com.open.lcp.framework.core.api.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.open.lcp.dao.entity.ApiMaxThreadsEntity;
import com.open.lcp.framework.core.api.service.ApiMaxThreadsService;
import com.open.lcp.framework.core.loader.ApiMaxThreadsTimerLoader;

@Service
public class ApiMaxThreadsServiceImpl implements ApiMaxThreadsService {

	@Autowired
	private ApiMaxThreadsTimerLoader apiMaxThreadsTimerLoader;

	@Override
	public ApiMaxThreadsEntity getLcpApiMaxThreads(String api) {
		return apiMaxThreadsTimerLoader.getLcpApiMaxThreads(api);
	}

}
