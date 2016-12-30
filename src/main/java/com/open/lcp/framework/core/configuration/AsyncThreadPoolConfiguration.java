package com.open.lcp.framework.core.configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @EnableAsync : �����첽�����Զ���ĳ�������õ������߳�ȥִ��.
 * 
 * @author hepengyuan
 *
 */
@Configuration
@EnableAsync
public class AsyncThreadPoolConfiguration {

	/** Set the ThreadPoolExecutor's core pool size. */
	private int corePoolSize = 200;
	/** Set the ThreadPoolExecutor's maximum pool size. */
	private int maxPoolSize = 200;
	/** Set the capacity for the ThreadPoolExecutor's BlockingQueue. */
	private int queueCapacity = 1000;

	private String ThreadNamePrefix = "MyLogExecutor-";

	@Bean
	public Executor logExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(corePoolSize);
		executor.setMaxPoolSize(maxPoolSize);
		executor.setQueueCapacity(queueCapacity);
		executor.setThreadNamePrefix(ThreadNamePrefix);

		// rejection-policy����pool�Ѿ��ﵽmax size��ʱ����δ���������
		// CALLER_RUNS���������߳���ִ�����񣬶����е��������ڵ��߳���ִ��
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		executor.initialize();
		return executor;
	}
}
