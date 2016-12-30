package com.open.lcp.framework.core.consts;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;

public final class LcpConstants {

	public final static String API_RESULT_MESSAGE_PREFIX = "api.result.msg.";

	public final static String REQ_ATTR_PerSMAP = "ps.map";
	public final static String REQ_API_METHOD_NAME = "api.methodname";

	// public static final String ATTRIB_STAT_EXT = "stat_ext";

	public static final String PARAM_GZ = "gz";
	public static final String PARAM_V = "v";
	public static final String PARAM_SIG = "sig";
	public static final String PARAM_DEVICEID = "deviceId";
	public static final String PARAM_APP_VERSION = "appVersion";
	public static final String PARAM_GUID = "guid";
	public static final String PARAM_JS_CALL = "jsCall";
	public static final String PARAM_LONG_2_STRING = "jsL2S";

	public static final String PARAM_OCTET_STREAM = "octet";

	public static final Gson gson = new GsonBuilder().disableHtmlEscaping().serializeSpecialFloatingPointValues().create();
	/** 所有long型值序列化为String */
	public static final Gson gsonL2S = new GsonBuilder().disableHtmlEscaping().serializeSpecialFloatingPointValues().setLongSerializationPolicy(LongSerializationPolicy.STRING)
			.create();

}
