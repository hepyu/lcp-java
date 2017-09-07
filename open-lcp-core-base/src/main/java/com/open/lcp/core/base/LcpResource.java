package com.open.lcp.core.base;

public enum LcpResource {

	// (1).lcp_framework resources
	mysql_lcp_framework_master("mysql", "/mysql/master", "framework", "mysql_lcp_framework"),

	// (2).lcp_passport resources
	mysql_lcp_passport_master("mysql", "/mysql/master", "passport", "mysql_lcp_passport"),

	redis_lcp_passport("redis", "/redis", "passport", "redis_lcp_passport"),

	@Deprecated
	ssdb_lcp_passport("ssdb", "/ssdb", "passport", "ssdb_lcp_passport"),

	// (3).lcp_biz resources
	mysql_lcp_biz_master("mysql", "/mysql/master", "biz", "mysql_lcp_biz"),

	redis_lcp_biz("redis", "/redis", "biz", "redis_lcp_biz"),

	// (4).lcp_comment resources
	hbase_lcp_biz_comment("hbase", "/hbase", "biz", "hbase_lcp_biz_comment"),

	redis_lcp_biz_comment("redis", "/redis", "biz", "redis_lcp_biz_comment"),

	// (5).lcp_lbs_resources
	mysql_lcp_lbs_master("mysql", "/mysql/master", "lbs", "mysql_lcp_lbs"),

	redis_lcp_lbs("redis", "/redis", "lbs", "redis_lcp_lbs");

	private String resourceType;

	private String zkNodeName;

	private String zkRelativePath;

	private String lcpAnnotationName;

	private LcpResource(String resourceType, String zkRelativePath, String zkNodeName, String lcpAnnotationName) {
		this.lcpAnnotationName = lcpAnnotationName;
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
