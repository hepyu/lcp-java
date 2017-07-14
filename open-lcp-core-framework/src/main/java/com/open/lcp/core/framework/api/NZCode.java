package com.open.lcp.core.framework.api;

/**
 * 非零错误码
 * 
 * @author
 */
public class NZCode {

	/** 非零错误码 */
	private int code;

	/** 原因，内部分析及开发用 */
	private String cause;

	/** 用户提示，展现用 */
	private String message;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getCause() {
		return cause;
	}

	public void setCause(String cause) {
		this.cause = cause;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "NZCode [code=" + code + ", cause=" + cause + ", message=" + message + "]";
	}

}
