package com.open.lcp.framework.core.facade;

import java.io.Serializable;

public class ApiResult implements Serializable {

	private static final long serialVersionUID = -2635713860423180667L;

	private int code;

	private Object data;

	private Object ext;

	public ApiResult(int resultCode, Object data, Object ext) {
		super();
		this.code = resultCode;
		this.data = data;
		this.ext = ext;
	}

	public ApiResult(int resultCode, Object data) {
		super();
		this.code = resultCode;
		this.data = data;
	}

	public ApiResult(int resultCode) {
		super();
		this.code = resultCode;
		this.data = "";
	}

	public ApiResult() {
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "ApiResultDto [code=" + code + ", data=" + data + "]";
	}

	/**
	 * @return the ext
	 */
	public Object getExt() {
		return ext;
	}

	/**
	 * @param ext
	 *            the ext to set
	 */
	public void setExt(Object ext) {
		this.ext = ext;
	}

}
