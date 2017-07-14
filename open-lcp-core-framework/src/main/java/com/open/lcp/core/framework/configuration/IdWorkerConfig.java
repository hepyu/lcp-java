package com.open.lcp.core.framework.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.open.lcp.core.framework.IdWorker;

@Configuration
public class IdWorkerConfig {

	@Value("${expression.workerId}")
	private int workerId;

	@Bean
	public IdWorker idWorker() {
		return new IdWorker(workerId);
	}

}
