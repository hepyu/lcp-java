package com.open.lcp.core.framework.api;

/**
 * @author
 */
public enum NZCodeBase {

	E_SYS_ERROR(1, "System service error"), //
	E_SYS_INVALID_PARAM(2, "Invalid parameter"), //
	E_SYS_PERMISSION_DENY(3, "permission denied"), //
	E_SYS_REQUEST_FREQUENCY(4, "User acting too frequently"), //
	E_SYS_RPC_ERROR(5, "RPC error"), //
	E_SYS_INVALID_APP_ID(6, "Invalid appId"), //
	E_SYS_INVALID_T(7, "Invalid ticket"), //
	E_SYS_INVALID_SIG(8, "Invalid signature"), //
	E_SYS_INVALID_VERSION(9, "Invalid version"), //
	E_SYS_UNKNOWN_METHOD(10, "Unknown API method request"), //
	E_SYS_UNKNOWN_RETURN_FORMAT(11, "Unknown results format"), //
	E_SYS_RPC_NULL(12, "service return null"), //
	E_SYS_UNSUPPORTED_FILE_TYPE(13, "Unsupported file type");

	private NZCodeBase(int code, String message) {
		this.code = code;
		this.message = message;
	}

	private int code;

	private String message;

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

}
