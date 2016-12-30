package com.open.lcp.framework.core.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.open.lcp.framework.core.dao.AppInfoDao;

/**
 * 
 * @author hepengyuan
 *
 */
@Configuration
public class LcpDaoConfiguration {

	@Bean
	public AppInfoDao getAppInfoDao() {
		return null;
	}
}
