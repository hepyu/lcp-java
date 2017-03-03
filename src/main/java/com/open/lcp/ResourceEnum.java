package com.open.lcp;

public enum ResourceEnum {

	ssdb_lcp_app_init("ssdb", "lcp", "lcp_app_init"),

	mysql_lcpFramework_master("mysql", "lcp", "lcp_framework"),

	mysql_lcpBiz_master("mysql", "lcp", "lcp_biz"),

	mysql_lcpBiz_slave("mysql", "lcp", "lcp_biz");

	private String resourceType;

	private String level;

	private String resourceName;

	private ResourceEnum(String resourceType, String level, String resourceName) {
		this.resourceType = resourceType;
		this.level = level;
		this.resourceName = resourceName;
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

}
