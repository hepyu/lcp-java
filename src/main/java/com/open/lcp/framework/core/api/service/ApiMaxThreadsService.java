package com.open.lcp.framework.core.api.service;

import com.open.lcp.dao.entity.ApiMaxThreadsEntity;

public interface ApiMaxThreadsService {

	ApiMaxThreadsEntity getLcpApiMaxThreads(String api);

}
