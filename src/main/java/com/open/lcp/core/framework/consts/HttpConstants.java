package com.open.lcp.core.framework.consts;

import java.util.Locale;

/**
 * 和http请求相关的常量，注意添加参数的时候，要改变不需要参加sig加密的参数
 * 
 */
public final class HttpConstants {
	public final static String PARAM_APP_ID = "appId";

	public final static String PARAM_VER = "v";

	public final static String PARAM_METHOD = "cmd_method";

	public final static String PARAM_TICKET = "t";
	public final static String PARAM_XUNLEI_TICKET = "xlt";
	public final static String PARAM_XUNLEI_UID = "xluid";

	public final static String PARAM_ACCESS_TOKEN = "accessToken";

	public final static String PARAM_SIG = "sig";

	public final static String PARAM_FORMAT = "format";

	public final static String PARAM_DATA_TYPE = "gz";

	// 不需要参加sig加密的参数
	public final static String[] NOT_ENCRYPTED_PARAMS = { PARAM_SIG };

	public static final String FORMAT_JSON = "json";

	public final static String DEFAULT_FORMAT = FORMAT_JSON;

	public final static String DATA_TYPE_COMPRESSION = "compression";

	public final static String PARAM_CLIENT_INFO = "clientInfo";

	public final static Locale DEFAULT_LANGUAGE = Locale.US;

	// public final static Set<String> platformParams = new HashSet<String>(); // 平台参数的key
	//
	// static {
	// platformParams.add(PARAM_APP_ID);
	// platformParams.add(PARAM_VER);
	// platformParams.add(PARAM_TICKET);
	// platformParams.add(PARAM_ACCESS_TOKEN);
	// platformParams.add(PARAM_SIG);
	// platformParams.add(PARAM_FORMAT);
	// platformParams.add(PARAM_DATA_TYPE);
	// platformParams.add(PARAM_CLIENT_INFO);
	// }
}
