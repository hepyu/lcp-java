package com.open.lcp.framework.core.api.service;

import com.open.lcp.framework.core.api.service.dao.entity.ApiMaxThreadsEntity;

public interface ApiMaxThreadsService {

	ApiMaxThreadsEntity getMcpApiMaxThreads(String api);

}
