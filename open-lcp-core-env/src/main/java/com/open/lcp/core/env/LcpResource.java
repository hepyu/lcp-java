package com.open.lcp.core.env;

import com.open.lcp.core.env.finder.EnvEnum;

//TODO:move to springcloud
public enum LcpResource {

	lcp_mysql_core_framework_master("mysql", "/mysql/master/core", "framework", "lcp_mysql_core_framework_master"), // dbAnnotationName_lcp_mysql_core_framework

	lcp_mysql_core_feature_user_master("mysql", "/mysql/master/core/feature", "user",
			"lcp_mysql_core_feature_user_master"), // dbAnnotationName_lcp_mysql_core_feature_user

	lcp_mysql_biz_master("mysql", "/mysql/master", "biz", "lcp_mysql_biz_master"),

	lcp_mysql_biz_lbs_master("mysql", "/mysql/master/biz", "lbs", "lcp_mysql_biz_lbs_master"),

	// redis实际使用中可能多个业务共用
	lcp_redis_core_feature_user("redis", "/redis/core/feature", "user", "lcp_redis_biz_user"),

	lcp_redis_biz("redis", "/redis", "biz", "lcp_redis_biz"),

	lcp_redis_biz_comment("redis", "/redis/biz", "comment", "lcp_redis_biz_comment"),

	lcp_redis_biz_lbs("redis", "/redis/biz", "lbs", "lcp_redis_biz_lbs"),

	// hbase
	lcp_hbase_biz_comment("hbase", "/hbase/biz", "comment", "lcp_hbase_biz_comment"), // 实际使用场景应该是/hbase,因为这是hbase的正确用法

	@Deprecated
	lcp_ssdb_core_feature_user("ssdb", "/ssdb/biz", "passport", "lcp_ssdb_core_feature_user");

	public final static String dbAnnotationName_lcp_mysql_core_framework_master = "lcp_mysql_core_framework_master";// 之所以在变量名中不加master，是因为以后可以改为主从自动识别.

	public final static String dbAnnotationName_lcp_mysql_core_feature_user_master = "lcp_mysql_core_feature_user_master";

	public final static String dbAnnotationName_lcp_mysql_biz_master = "lcp_mysql_biz_master";

	public final static String dbAnnotationName_lcp_mysql_biz_comment_master = dbAnnotationName_lcp_mysql_biz_master;

	public final static String dbAnnotationName_lcp_mysql_biz_lbs_master = "lcp_mysql_biz_lbs_master";

	public final static String cacheAnnotationName_lcp_redis_core_feature_user = "lcp_redis_core_feature_user";

	public final static String cacheAnnotationName_lcp_redis_biz = "lcp_redis_biz";

	public final static String cacheAnnotationName_lcp_redis_biz_comment = "lcp_redis_biz_comment";

	public final static String cacheAnnotationName_lcp_redis_biz_lbs = "lcp_redis_biz_lbs";

	@Deprecated
	public final static String cacheAnnotationName_lcp_ssdb_core_feature_user = "lcp_ssdb_core_feature_user";

	public final static String bigDataStorageAnnotationName_lcp_hbase_biz_comment = "lcp_hbase_biz_comment";

	public static final String RESOURCE_ROOT = "/lcp";

	private String resourceType;

	private String parentRelativePath;

	private String nodeName;

	private String classDeclaredDataSourceAnnotationName;

	private LcpResource(String resourceType, String parentRelativePath, String nodeName,
			String classDeclaredDataSourceAnnotationName) {
		this.resourceType = resourceType;
		this.parentRelativePath = parentRelativePath;
		this.nodeName = nodeName;
		this.classDeclaredDataSourceAnnotationName = classDeclaredDataSourceAnnotationName;
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

	public String getClassDeclaredDataSourceAnnotationName() {
		return classDeclaredDataSourceAnnotationName;
	}

	public String getAbsolutePath(EnvEnum env) {
		return new StringBuilder().append(RESOURCE_ROOT).append("/").append(env.toString())
				.append(this.parentRelativePath).append("/").append(this.nodeName).toString();
	}

	public String getAbsoluteParentPath(EnvEnum env) {
		return new StringBuilder().append(RESOURCE_ROOT).append("/").append(env.toString())
				.append(this.parentRelativePath).toString();
	}

}
