package com.open.lcp.passport;

public class PassportException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2691589501039431902L;

	private int passportCode;

	// 登录失败
	public static final int EXCEPTION_LOGIN_FAILED = 9001;

	// 没有登录，需要登�?
	public static final int EXCEPTION_NEED_LOGIN = 9002;

	// 账号已经绑定或�?�当前账号已经绑定了同类型的账号
	public static final int EXCEPTION_BIND_ACCOUNT_HAS_EXIST_OR_SAME_TYPE_HAS_EXIST = 9003;

	// ticket无效
	public static final int EXCEPTION_TICKET_INVALID = 9004;

	// 手机验证码发送失�?
	public static final int EXCEPTION_SEND_MOBILE_CODE_FAILED = 9005;

	// 手机验证码失�?
	public static final int EXCEPTION_MOBILE_CODE_INVALID = 9006;

	// 手机验证码类型非�?
	public static final int EXCEPTION_MOBILE_CODE_TYPE_INVALID = 9007;

	// 用户身份可疑
	public static final int EXCEPTION_XUNLEI_USERID_ERROR = 9008;

	// 获取用户肖像失败
	public static final int EXCEPTION_OBTAIN_PORTRAIT_FAILED = 9009;

	// 用户身份可疑
	public static final int EXCEPTION_XL_RETURN_USERID_NULL = 9010;

	// 非法的用户账号类�?
	public static final int EXCEPTION_INVALID_ACCOUNT_TYPE = 9011;

	// 调用迅雷用户中心接口失败
	public static final int EXCEPTION_REGIST_XL_CENTER_FAILED = 9012;

	// 用户唯一标示验证失败
	public static final int EXCEPTION_CHECK_USER_IDENTIFIER_FAILED = 9013;

	public static final int EXCEPTION_NO_SUPPORT_METHOD = 9014;

	public static final int EXCEPTION_PARAM_INVALID = 9015;

	public static final int EXCEPTION_OBTAIN_OPEN_ID_FAILED = 9016;

	public static final int EXCEPTION_OPEN_ID_INVALID = 9017;

	public static final int EXCEPTION_ELEMENT_TOO_MANY = 9018;

	public static final int EXCEPTION_USER_ACCOUNT_NOT_EXIST = 9019;

	// account not exist.
	public static final int EXCEPTION_ACCOUNT_NOT_EXIST = 9020;

	// public PassportException(int passportCode) {
	// super(passportCode + "");
	// this.passportCode = passportCode;
	// }

	public PassportException(int passportCode, Throwable t) {
		super(passportCode + "", t);
		this.passportCode = passportCode;
	}

	public PassportException(int passportCode, String msg, Throwable t) {
		super(msg, t);
		this.passportCode = passportCode;
	}

	public int getPassportCode() {
		return passportCode;
	}

	/*
	 * @Override public String getMessage() { String errorMsg = "passportCode:"
	 * + passportCode + ";ExceptionMsg:" + super.getMessage(); return errorMsg;
	 * }
	 */

}
