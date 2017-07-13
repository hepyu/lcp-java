package com.open.lcp.core.framework.api.service;

import com.open.lcp.core.framework.api.service.dao.entity.ApiMaxThreadsEntity;

public interface ApiMaxThreadsService {

	ApiMaxThreadsEntity getLcpApiMaxThreads(String api);

}
