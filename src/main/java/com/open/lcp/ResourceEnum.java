package com.open.lcp;

public enum ResourceEnum {

	mysql_lcp_framework_master("mysql", "lcp_framework", "lcp_framework_master"),
	
	mysql_lcp_plugin_passport_master("mysql", "lcp_plugin", "lcp_plugin_passport_master"),
	
	mysql_lcp_plugin_appinit_master("mysql", "lcp_plugin", "lcp_plugin_appinit_master"),
	
	redis_lcp_plugin_app_init("redis", "lcp_plugin", "lcp_app_init"),

	mysql_lcp_biz_master("mysql", "lcp_biz", "lcp_biz_master"),

	mysql_lcp_biz_slave("mysql", "lcp_biz", "lcp_biz_slave");

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
