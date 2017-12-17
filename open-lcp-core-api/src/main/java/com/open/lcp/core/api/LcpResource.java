package com.open.lcp.core.api;

public enum LcpResource {

	// (1).lcp_framework resources
	mysql_lcp_framework_master("mysql", "/mysql/master", "framework"),

	// (2).lcp_passport resources
	mysql_lcp_passport_master("mysql", "/mysql/master", "passport"),

	redis_lcp_passport("redis", "/redis", "passport"),

	@Deprecated
	ssdb_lcp_passport("ssdb", "/ssdb", "passport"),

	// (3).lcp_biz resources
	mysql_lcp_biz_master("mysql", "/mysql/master", "biz"),

	redis_lcp_biz("redis", "/redis", "biz"),

	// (4).lcp_comment resources
	hbase_lcp_biz_comment("hbase", "/hbase", "biz"),

	redis_lcp_biz_comment("redis", "/redis", "biz"),

	// (5).lcp_lbs_resources
	mysql_lcp_lbs_master("mysql", "/mysql/master", "lbs"),

	redis_lcp_lbs("redis", "/redis", "lbs");

	private String resourceType;

	private String zkNodeName;

	private String zkRelativePath;

	private String lcpAnnotationName;

	private LcpResource(String resourceType, String zkRelativePath, String zkNodeName) {
		this.resourceType = resourceType;
		this.zkNodeName = zkNodeName;
		this.zkRelativePath = zkRelativePath;
	}

	public String resourceType() {
		return resourceType;
	}

	public String zkNodeName() {
		return zkNodeName;
	}

	public String zkRelativePath() {
		return zkRelativePath;
	}

	public String lcpAnnotationName() {
		return lcpAnnotationName;
	}

}
