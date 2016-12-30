package com.open.lcp.framework.core.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.open.lcp.framework.core.dao.AppInfoDao;
import com.open.lcp.framework.core.dao.AppInitInfoDao;

/**
 * 
 * @author hepengyuan
 *
 */
@Configuration
public class LcpDaoConfiguration {

	@Bean
	public AppInfoDao getAppInfoDao() throws Exception {
		return null;
	}

	@Bean
	public AppInitInfoDao getAppInitInfoDao() throws Exception {
		return null;
	}

}
