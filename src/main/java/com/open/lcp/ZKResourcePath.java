package com.open.lcp;

public enum ZKResourcePath {

	mysql_lcp_framework_master("mysql", "framework", "/master", "framework"),

	mysql_lcp_passport_master("mysql", "passport", "/master", "passport"),

	mysql_lcp_app_init_master("mysql", "app_init", "/master", "biz"),

	redis_lcp_passport("redis", "passport", "", "passport"),

	redis_lcp_app_init("redis", "app_init", "", "app_init"),

	@Deprecated
	ssdb_lcp_passport("ssdb", "passport", "", "passport");

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
