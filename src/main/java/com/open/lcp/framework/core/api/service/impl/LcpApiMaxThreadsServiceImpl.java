package com.open.lcp.framework.core.api.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.open.lcp.framework.core.api.service.LcpApiMaxThreadsService;
import com.open.lcp.framework.core.api.service.dao.entity.LcpApiMaxThreadsEntity;
import com.open.lcp.framework.core.loader.ApiMaxThreadsTimerLoader;

@Service
public class LcpApiMaxThreadsServiceImpl implements LcpApiMaxThreadsService {

	@Autowired
	private ApiMaxThreadsTimerLoader apiMaxThreadsTimerLoader;

	@Override
	public LcpApiMaxThreadsEntity getLcpApiMaxThreads(String api) {
		return apiMaxThreadsTimerLoader.getLcpApiMaxThreads(api);
	}

}
