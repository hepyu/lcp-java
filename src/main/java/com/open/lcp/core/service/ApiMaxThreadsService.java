package com.open.lcp.core.service;

import com.open.lcp.core.dao.entity.ApiMaxThreadsEntity;

public interface ApiMaxThreadsService {

	ApiMaxThreadsEntity getMcpApiMaxThreads(String api);

}
