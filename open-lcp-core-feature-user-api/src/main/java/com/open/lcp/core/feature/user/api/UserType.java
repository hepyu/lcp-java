package com.open.lcp.core.feature.user.api;

public enum UserType {

	unknow(-1, UserCategory.ACCOUNT_CATEGORY_UNKNOW),
	// 微信账号，使用中
	weichat(0, UserCategory.ACCOUNT_CATEGORY_USER),
	// 小米账号，使用中
	xiaomi(1, UserCategory.ACCOUNT_CATEGORY_USER),
	// 手机账号，使用中
	mobile(2, UserCategory.ACCOUNT_CATEGORY_USER),
	// QQ账号，保留字段
	qq(3, UserCategory.ACCOUNT_CATEGORY_USER),
	// 测试账号，保留字段
	test(4, UserCategory.ACCOUNT_CATEGORY_TEST),
	// 假账号，保留字段
	fake(5, UserCategory.ACCOUNT_CATEGORY_FAKE),
	// 普通机器人账号，使用中
	robot(6, UserCategory.ACCOUNT_CATEGORY_ROBOT),
	// weibo
	weibo(7, UserCategory.ACCOUNT_CATEGORY_USER);

	private int type = 0;
	private String category;

	private UserType(int type, String category) {
		this.type = type;
		this.category = category;
	}

	public int type() {
		return this.type;
	}

	public String category() {
		return this.category;
	}

	public static UserType valueOf(int value) { // 手写的从int到enum的转换函数
		switch (value) {
		case 0:
			return weichat;
		case 1:
			return xiaomi;
		case 2:
			return mobile;
		case 3:
			return qq;
		case 4:
			return test;
		case 5:
			return fake;
		case 6:
			return robot;
		case 7:
			return weibo;
		default:
			return unknow;
		}
	}

}
