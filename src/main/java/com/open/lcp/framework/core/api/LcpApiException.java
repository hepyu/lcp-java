package com.open.lcp.framework.core.api;

/**
 * @author
 */
public class LcpApiException extends RuntimeException {

	public static final LcpApiException E_SYS_ERROR = new LcpApiException(NZCodeBase.E_SYS_ERROR.getCode(),
			NZCodeBase.E_SYS_ERROR.getMessage());

	public static final LcpApiException E_SYS_INVALID_PARAM = new LcpApiException(NZCodeBase.E_SYS_INVALID_PARAM.getCode(),
			NZCodeBase.E_SYS_INVALID_PARAM.getMessage());

	public static final LcpApiException E_SYS_PERMISSION_DENY = new LcpApiException(
			NZCodeBase.E_SYS_PERMISSION_DENY.getCode(), NZCodeBase.E_SYS_PERMISSION_DENY.getMessage());

	public static final LcpApiException E_SYS_REQUEST_FREQUENCY = new LcpApiException(
			NZCodeBase.E_SYS_REQUEST_FREQUENCY.getCode(), NZCodeBase.E_SYS_REQUEST_FREQUENCY.getMessage());

	public static final LcpApiException E_SYS_RPC_ERROR = new LcpApiException(NZCodeBase.E_SYS_RPC_ERROR.getCode(),
			NZCodeBase.E_SYS_RPC_ERROR.getMessage());

	public static final LcpApiException E_SYS_INVALID_APP_ID = new LcpApiException(NZCodeBase.E_SYS_INVALID_APP_ID.getCode(),
			NZCodeBase.E_SYS_INVALID_APP_ID.getMessage());

	public static final LcpApiException E_SYS_INVALID_T = new LcpApiException(NZCodeBase.E_SYS_INVALID_T.getCode(),
			NZCodeBase.E_SYS_INVALID_T.getMessage());

	public static final LcpApiException E_SYS_INVALID_SIG = new LcpApiException(NZCodeBase.E_SYS_INVALID_SIG.getCode(),
			NZCodeBase.E_SYS_INVALID_SIG.getMessage());

	public static final LcpApiException E_SYS_INVALID_VERSION = new LcpApiException(
			NZCodeBase.E_SYS_INVALID_VERSION.getCode(), NZCodeBase.E_SYS_INVALID_VERSION.getMessage());

	public static final LcpApiException E_SYS_UNKNOWN_METHOD = new LcpApiException(NZCodeBase.E_SYS_UNKNOWN_METHOD.getCode(),
			NZCodeBase.E_SYS_UNKNOWN_METHOD.getMessage());

	public static final LcpApiException E_SYS_UNKNOWN_RETURN_FORMAT = new LcpApiException(
			NZCodeBase.E_SYS_UNKNOWN_RETURN_FORMAT.getCode(), NZCodeBase.E_SYS_UNKNOWN_RETURN_FORMAT.getMessage());

	public static final LcpApiException E_SYS_RPC_NULL = new LcpApiException(NZCodeBase.E_SYS_RPC_NULL.getCode(),
			NZCodeBase.E_SYS_RPC_NULL.getMessage());

	public static final LcpApiException E_SYS_UNSUPPORTED_FILE_TYPE = new LcpApiException(
			NZCodeBase.E_SYS_UNSUPPORTED_FILE_TYPE.getCode(), NZCodeBase.E_SYS_UNSUPPORTED_FILE_TYPE.getMessage());

	public LcpApiException(int errorCode) {
		this(errorCode, null);
	}

	public LcpApiException(int errorCode, String errorMsg) {
		super(errorMsg);
		this.errorCode = errorCode;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 2087324803402274230L;

	private int errorCode;

	public int getErrorCode() {
		return errorCode;
	}

	private Object ext;

	public Object getExt() {
		return ext;
	}

	public void setExt(Object ext) {
		this.ext = ext;
	}

}
