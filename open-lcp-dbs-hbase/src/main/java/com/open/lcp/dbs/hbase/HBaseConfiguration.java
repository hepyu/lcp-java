package com.open.lcp.dbs.hbase;

import org.apache.hadoop.hbase.client.Connection;
import org.springframework.context.annotation.Bean;

import com.open.lcp.core.base.LcpResource;

public class HBaseConfiguration {

	@Bean(name = "hbase_comment")
	public Connection hbaseConnection() {
		return HBaseXFactory.createConnection(LcpResource.hbase_lcp_biz_comment);
	}
}
