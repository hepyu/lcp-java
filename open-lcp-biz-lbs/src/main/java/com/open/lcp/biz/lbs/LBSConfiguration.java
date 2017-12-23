package com.open.lcp.biz.lbs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.open.lcp.core.env.LcpResource;
import com.open.lcp.dbs.cache.redis.RedisX;
import com.open.lcp.dbs.cache.redis.RedisXFactory;

@Configuration
public class LBSConfiguration {

	// private final static String BEAN_NAME_HBASE_COMMENT = "hbase_comment";

	public final static String BEAN_NAME_REDIS_LBS = LcpResource.cacheAnnotationName_lcp_redis_biz_lbs;
	
	@Bean(name = LcpResource.cacheAnnotationName_lcp_redis_biz_lbs)
	public RedisX getLBSRedis() {
		return RedisXFactory.loadRedisX(LcpResource.lcp_redis_biz_lbs);
	}

	// @Bean(name = "commentSecureManager")
	// public SecureManager getSecureManager() {
	// return SecureManager.getInstance();
	// }
}
