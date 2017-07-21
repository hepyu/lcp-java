package com.open.lcp.biz.comment;

import org.apache.hadoop.hbase.client.Connection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.open.lcp.core.base.LcpResource;
import com.open.lcp.dbs.cache.redis.RedisX;
import com.open.lcp.dbs.cache.redis.RedisXFactory;
import com.open.lcp.dbs.hbase.HBaseXFactory;

@Configuration
public class CommentConfiguration {

	private final static String BEAN_NAME_HBASE_COMMENT = "hbase_comment";

	private final static String BEAN_NAME_REDIS_COMMENT = "redis_comment";

	@Bean(name = BEAN_NAME_HBASE_COMMENT)
	public Connection hbaseConnection() {
		return HBaseXFactory.createConnection(LcpResource.hbase_lcp_biz_comment);
	}

	@Bean(name = BEAN_NAME_REDIS_COMMENT)
	public RedisX getCommentListRedis() {
		return RedisXFactory.loadRedisX(LcpResource.redis_lcp_biz_comment);
	}

	// @Bean(name = "commentSecureManager")
	// public SecureManager getSecureManager() {
	// return SecureManager.getInstance();
	// }
}
