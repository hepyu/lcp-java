package com.open.lcp.core.env;

import com.open.lcp.core.env.finder.EnvEnum;

public enum LcpResource {

	mysql_lcp_framework_master("mysql", "/mysql/master","framework"),
	mysql_lcp_biz_master("mysql", "/mysql/master","biz"),
	mysql_lcp_passport_master("mysql", "/mysql/master/biz","passport"),
	mysql_lcp_biz_lbs_master("mysql", "/mysql/master/biz","lbs"),
	
	//redis实际使用中可能多个业务共用
	redis_lcp_biz("redis", "/redis","biz"),
	redis_lcp_biz_passport("redis", "/redis/biz","passport"),
	redis_lcp_biz_comment("redis", "/redis/biz","comment"),
	redis_lcp_biz_lbs("redis", "/redis/biz","lbs"),

	hbase_lcp_biz_comment("hbase", "/hbase/biz","comment"),//实际使用场景应该是/hbase,因为这是hbase的正确用法
	
	@Deprecated
	ssdb_lcp_passport("ssdb", "/ssdb/biz","passport");

	public static final String RESOURCE_ROOT = "/lcp";
	
	private String resourceType;

	private String parentRelativePath;
	
	private String nodeName;

	private LcpResource(String resourceType, String parentRelativePath, String nodeName) {
		this.resourceType = resourceType;
		this.parentRelativePath = parentRelativePath;
		this.nodeName = nodeName;
	}

	public String resourceType() {
		return resourceType;
	}

	public String getParentRelativePath() {
		return parentRelativePath;
	}

	public String getNodeName() {
		return nodeName;
	}

	public String getAbsolutePath(EnvEnum env){
		return new StringBuilder().append(RESOURCE_ROOT).append("/").append(env.toString()).append(this.parentRelativePath).append("/").append(this.nodeName).toString();
	}
	
	public String getAbsoluteParentPath(EnvEnum env){
		return new StringBuilder().append(RESOURCE_ROOT).append("/").append(env.toString()).append(this.parentRelativePath).toString();
	}

}
