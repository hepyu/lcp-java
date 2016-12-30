package com.open.lcp.framework.core.service;

import com.open.lcp.framework.core.dao.entity.ApiMaxThreadsEntity;

public interface ApiMaxThreadsService {

	ApiMaxThreadsEntity getMcpApiMaxThreads(String api);

}
