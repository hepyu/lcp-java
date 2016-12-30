package com.open.lcp.core.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.open.lcp.core.dao.AppInfoDao;

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
