package com.open.lcp;

public enum ZKResourcePath {

	//(1).lcp_framework resources
	mysql_lcp_framework_master("mysql", "framework", "/master", "framework"),

	//(2).lcp_passport resources
	mysql_lcp_passport_master("mysql", "passport", "/master", "passport"),
	
	redis_lcp_passport("redis", "passport", "", "passport"),	//废弃
	
	@Deprecated
	ssdb_lcp_passport("ssdb", "passport", "", "passport"),

	//(3).lcp_biz resources
	mysql_lcp_biz_master("mysql", "biz", "/master", "biz"),

	redis_lcp_biz("redis", "biz", "", "biz");

	private String resourceType;

	private String level;

	private String resourceName;

	private String relativePath;

	private ZKResourcePath(String resourceType, String level, String relativePath, String resourceName) {
		this.resourceType = resourceType;
		this.level = level;
		this.resourceName = resourceName;
		this.relativePath = relativePath;
	}

	public String resourceType() {
		return resourceType;
	}

	public String resourceName() {
		return resourceName;
	}

	public String level() {
		return level;
	}

	public String relativePath() {
		return relativePath;
	}

}
