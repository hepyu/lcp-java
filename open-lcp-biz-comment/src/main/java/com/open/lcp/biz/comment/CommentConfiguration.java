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

	public final static String BEAN_NAME_HBASE_COMMENT = "hbase_comment";

	public final static String BEAN_NAME_COMMENT_DISTRIBUTED_CACHE = "comment_distributed_cache";

	@Bean(name = BEAN_NAME_HBASE_COMMENT)
	public Connection hbaseConnection() {
		return HBaseXFactory.createConnection(LcpResource.hbase_lcp_biz_comment);
	}

	@Bean(name = BEAN_NAME_COMMENT_DISTRIBUTED_CACHE)
	public RedisX getCommentListRedis() {
		return RedisXFactory.loadRedisX(LcpResource.redis_lcp_biz_comment);
	}

	// @Bean(name = "commentSecureManager")
	// public SecureManager getSecureManager() {
	// return SecureManager.getInstance();
	// }
}
