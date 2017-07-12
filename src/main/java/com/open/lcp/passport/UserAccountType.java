package com.open.lcp.passport;

public enum UserAccountType {

	unknow(-1,"unknow"),
	// 微信账号，使用中
	weichat(0,"user"),
	// 小米账号，使用中
	xiaomi(1,"user"),
	// 手机账号，使用中
	mobile(2,"user"),
	// QQ账号，保留字段
	qq(3,"user"),
	// 测试账号，保留字段
	test(4,"user"),
	// 假账号，保留字段
	fake(5,"user"),
	// 普通机器人账号，使用中
	robbot(6,"jqr"),
	// weibo
	weibo(7,"user");

	private int value = 0;
	private String category;

	private UserAccountType(int value,String category) {
		this.value = value;
		this.category = category;
	}

	public int value() {
		return this.value;
	}
	public String category() {
		return this.category;
	}

	public static UserAccountType valueOf(int value) { // 手写的从int到enum的转换函数
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
			return robbot;
		case 7:
			return weibo;
		default:
			return unknow;
		}
	}

}
