package com.open.lcp.core.base;

public enum LcpResource {

	// (1).lcp_framework resources
	mysql_lcp_framework_master("mysql", "/mysql/master", "framework", "mysql_lcp_framework"),

	// (2).lcp_passport resources
	mysql_lcp_passport_master("mysql", "/mysql/master", "passport", "mysql_lcp_passport"),

	redis_lcp_passport("redis", "/redis", "passport", "redis_lcp_passport"), // 废弃

	@Deprecated
	ssdb_lcp_passport("ssdb", "/ssdb", "passport", "ssdb_lcp_passport"),

	// (3).lcp_biz resources
	mysql_lcp_biz_master("mysql", "/mysql/master", "biz", "mysql_lcp_biz"),

	redis_lcp_biz("redis", "/redis", "biz", "redis_lcp_biz");

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
