package com.open.lcp.framework.util;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.open.lcp.framework.core.consts.HttpConstants;

public class LcpUtils {

	private static final Log logger = LogFactory.getLog(LcpUtils.class);

	private static final Pattern ipPattern = Pattern.compile("([0-9]{1,3}\\.){3}[0-9]{1,3}");

	public static Gson gson = new GsonBuilder().disableHtmlEscaping().serializeSpecialFloatingPointValues().create();

	public static Gson gsonOnlyExpose = new GsonBuilder().disableHtmlEscaping().excludeFieldsWithoutExposeAnnotation()
			.create();

	public static long rpcTimeCost(long t, Object msg) {
		return logTimeCost(t, "RPC_CALL " + msg, logger);
	}

	public static long logTimeCost(long t, Object msg) {
		return logTimeCost(t, msg, logger);
	}

	public static long logTimeCost(long t, Object msg, Log logger) {
		long et = System.currentTimeMillis();
		if (logger.isDebugEnabled()) {
			logger.debug(msg + " timeCost:" + (et - t));
		}
		return et;
	}

	/**
	 * 生成密钥
	 * 
	 * @return
	 */
	public static String generateSecretKey() {
		UUID uuid = UUID.randomUUID();
		long now = System.currentTimeMillis();
		return DigestUtils.md5Hex(uuid.toString() + now);
	}

	/**
	 * 提供给app.init接口用，imei和mac的解密。
	 * 
	 * 先base64 decode，然后用key的md5后的16byte来解密，得到的byte[]直接new String。
	 * 
	 * @param srcBase64
	 * @param key
	 * @return
	 */
	public static String imeiAesDecode(String srcBase64, String key) {
		try {
			final byte[] inBytes = Base64Utils.decode(srcBase64);
			final byte[] outBytes = AESUtils.decrypt(inBytes, DigestUtils.md5(key));
			return new String(outBytes);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 提供给app.init接口用，imei和mac的加密
	 * 
	 * @param content
	 *            原文 mac去掉冒号和减号，转成大写
	 * @param key
	 * @return
	 */
	public static String imeiAesEncode(String content, String key) {
		try {
			final byte[] outBytes = AESUtils.encrypt(content.getBytes(), DigestUtils.md5(key));
			return Base64Utils.encode(outBytes);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * check if response require compression
	 * 
	 * @param requestParamMap
	 * @return
	 */
	public static boolean isResponseCompressionRequired(Map<String, String> requestParamMap) {
		String dataType = requestParamMap.get(HttpConstants.PARAM_DATA_TYPE);
		return StringUtils.equalsIgnoreCase(dataType, HttpConstants.DATA_TYPE_COMPRESSION);
	}

	/**
	 * 填充http参数
	 * 
	 * @param request
	 * @return
	 */
	public static Map<String, String> fillParamMap(HttpServletRequest request) {
		Map<String, String> requestParamsMap = new HashMap<String, String>();
		// 填充header
		// String clientIp = McpUtils.getRemoteAddr(request);
		// String userAgent = request.getHeader("user-agent");
		// String language = request.getHeader("Accept-Language");
		// language = checkLanguage(language);
		// requestParamsMap.put(HttpConstants.CLIENT_IP, clientIp);
		// requestParamsMap.put(HttpConstants.USER_AGENT, userAgent);
		// requestParamsMap.put(HttpConstants.LANGUAGE, language);
		// 填充请求参数
		Enumeration<String> e = request.getParameterNames();

		while (e.hasMoreElements()) {
			String param = e.nextElement();
			String value = request.getParameter(param);
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("[McpUtils]:{getRequestParamMap(HttpServletRequest):[param=%s]=>[value=%s]}",
						param, value));
			}
			if (value != null) {
				requestParamsMap.put(param, value);
			}
		}
		return requestParamsMap;
	}

	public static Map<String, String> getHttpHeads(HttpServletRequest request) {
		final Map<String, String> heads = new HashMap<String, String>();
		final Enumeration<?> hs = request.getHeaderNames();
		while (hs.hasMoreElements()) {
			String name = (String) hs.nextElement();
			heads.put(name, request.getHeader(name));
		}
		return heads;
	}

	public static String mapToString(Map<String, String> maps) {
		if (maps == null)
			return null;
		if (maps.isEmpty())
			return "";
		final StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> e : maps.entrySet()) {
			if (sb.length() > 0)
				sb.append("\r\n");
			sb.append(e.getKey());
			sb.append(": ");
			sb.append(e.getValue());
		}
		return sb.toString();
	}

	/**
	 * 获得http输出格式
	 * 
	 * @param requestParamMap
	 * @return
	 */
	public static String getResponseFormat(Map<String, String> requestParamMap) {
		String format = requestParamMap.get(HttpConstants.PARAM_FORMAT);
		if (StringUtils.equalsIgnoreCase(format, HttpConstants.FORMAT_JSON)) {
			return HttpConstants.FORMAT_JSON;
		} else {
			return HttpConstants.DEFAULT_FORMAT;
		}
	}

	public static String getCmdMethodFromURI(final String requestURI) {
		if (requestURI == null || requestURI.length() == 0) {
			return "";
		}
		String requestMethod = requestURI;
		if (requestMethod.matches("(/\\w+){2,15}")) {
			if (requestMethod.startsWith("/api/")) {
				requestMethod = requestMethod.substring(5);
			} else if (requestMethod.startsWith("/api2/")) {
				requestMethod = requestMethod.substring(6);
			} else {
				requestMethod = requestMethod.substring(1);
			}
			while (requestMethod.endsWith("/")) {
				requestMethod = requestMethod.substring(0, requestMethod.length() - 1);
			}
			final String method = requestMethod.replace('/', '.');
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("[McpUtils]:{requestParamMapFix: [requestURI=%s] => [method=%s]}",
						requestURI, method));
			}
			return method;
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("[McpUtils]:{requestParamMapFix not rest: [requestURI=%s]}", requestURI));
			}
		}
		return "";
	}

	/**
	 * 左匹配，右边以*结尾代表模糊匹配。admin相关的接口，不允许通过全匹配的*（只有一个*）授权。
	 * 
	 * @param srcMethodName
	 * @param wildcardMethodName
	 * @return
	 */
	public static boolean leftMatch(String srcMethodName, String wildcardMethodName) {
		if (srcMethodName == null && wildcardMethodName == null) {
			return true;// 全是null，确实匹配。
		}
		if (srcMethodName == null || wildcardMethodName == null) {
			return false;// 有一个是null，不匹配。
		}
		if (srcMethodName.length() == 0 && wildcardMethodName.length() == 0) {
			return true;// 全是空串，确实匹配。
		}
		if (wildcardMethodName.length() == 0) {
			return false;// 匹配串是空串，不匹配。
		}
		if (srcMethodName.startsWith("admin.") && wildcardMethodName.equals("*")) {
			return false;
		}
		if (wildcardMethodName.endsWith("*")) {
			wildcardMethodName = wildcardMethodName.substring(0, wildcardMethodName.length() - 1);
			return srcMethodName.startsWith(wildcardMethodName);
		}
		return srcMethodName.equalsIgnoreCase(wildcardMethodName);
	}

	/**
	 * 用Gson的fromJson转换
	 * 
	 * @param <T>
	 * @param json
	 * @param t
	 * @return
	 */
	public static <T> T toObjectSafe(String json, Class<T> t) {
		if (json == null || json.length() == 0) {
			return null;
		}
		return gson.fromJson(json, t);
	}

	/**
	 * 对http请求参数作字典排序，拼接字符串
	 * 
	 * @param paramMap
	 * @param sigParamKey
	 * @return
	 */
	public final static String generateNormalizedString(final Map<String, String> httpHeads,
			Map<String, String> paramMap) {
		Set<String> params = paramMap.keySet();
		List<String> sortedParams = new ArrayList<String>(params);
		Collections.sort(sortedParams);
		StringBuilder sb = new StringBuilder();
		boolean isHeadError = false;
		for (String paramKey : sortedParams) {
			if (!needEncryptedForSig(paramKey)) {
				continue;
			}
			sb.append(paramKey).append('=').append(paramMap.get(paramKey));
		}
		if (isHeadError) {// 对http head异常做兼容
			params = paramMap.keySet();
			sortedParams = new ArrayList<String>(params);
			Collections.sort(sortedParams);
			sb = new StringBuilder();
			for (String paramKey : sortedParams) {
				if (!needEncryptedForSig(paramKey)) {
					continue;
				}
				sb.append(paramKey).append('=').append(paramMap.get(paramKey));
			}
		}
		return sb.toString();
	}

	private static boolean needEncryptedForSig(String paramKey) {
		for (String notEncryptedParam : HttpConstants.NOT_ENCRYPTED_PARAMS) {
			if (paramKey.equals(notEncryptedParam)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * build query string with request parameters map, POST method is also
	 * supported
	 * 
	 * @param requestParamMap
	 * @return
	 */
	public final static String buildQueryString(Map<String, String> requestParamMap) {
		List<String> arr = new ArrayList<String>();
		Set<Entry<String, String>> params = requestParamMap.entrySet();
		for (Entry<String, String> kv : params) {
			StringBuffer sb = new StringBuffer();
			sb.append(kv.getKey());
			sb.append('=');
			try {
				String v = URLEncoder.encode(StringUtils.defaultString(kv.getValue()), CharEncoding.UTF_8);
				sb.append(v);
			} catch (UnsupportedEncodingException e) {
				logger.error("buildQueryString(Map<String,String>)", e);
			}
			arr.add(sb.toString());
		}
		return StringUtils.join(arr, '&');
	}

	/**
	 * get the md5 signature
	 * 
	 * @param normalizedString
	 * @param secretKey
	 * @return
	 */
	public final static String generateSignature(String normalizedString, String secretKey) {
		return DigestUtils.md5Hex(normalizedString + secretKey).toLowerCase();
	}

	/**
	 * 取x -froward
	 * 
	 * @param request
	 * @return
	 */
	public static String getRemoteAddrAll(HttpServletRequest request) {
		Enumeration<String> xffs = request.getHeaders("X-Forwarded-For");
		String ip = "";
		if (xffs.hasMoreElements()) {
			ip = xffs.nextElement();
		}

		if (!ip.equals("")) {
			ip = ip + "," + request.getRemoteAddr();
		} else {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	/**
	 * 取客户端的真实IP，考虑了反向代理等因素的干扰
	 * 
	 * @param request
	 * @return
	 */
	public static String getRemoteAddr(HttpServletRequest request) {
		String ip;
		Enumeration<String> xffs = request.getHeaders("X-Forwarded-For");
		if (xffs.hasMoreElements()) {
			String xff = xffs.nextElement();
			ip = resolveClientIPFromXFF(xff);
			if (isValidIP(ip)) {
				// if (logger.isInfoEnabled()) {
				// logger.info("X-Forwarded-For" + ip);
				// }
				return ip;
			}
		}
		ip = request.getHeader("Proxy-Client-IP");
		if (isValidIP(ip)) {
			// if (logger.isInfoEnabled()) {
			// logger.info("Proxy-Client-IP" + ip);
			// }
			return ip;
		}
		ip = request.getHeader("WL-Proxy-Client-IP");
		if (isValidIP(ip)) {
			// if (logger.isInfoEnabled()) {
			// logger.info("WL-Proxy-Client-IP" + ip);
			// }
			return ip;
		}
		// if (logger.isInfoEnabled()) {
		// logger.info("None-Proxy-Client-IP" + ip);
		// }
		return request.getRemoteAddr();
	}

	public static int getRemotePort(HttpServletRequest request) {
		String port;
		Enumeration<String> xffs = request.getHeaders("x-real-port");
		if (xffs.hasMoreElements()) {
			port = xffs.nextElement();
			if (port.matches("\\d{1,9}")) {
				return Integer.valueOf(port);
			}
		}
		return 0;
	}

	/**
	 * 从X-Forwarded-For头部中获取客户端的真实IP。 X-Forwarded-For并不是RFC定义的标准HTTP请求Header
	 * ，可以参考http://en.wikipedia.org/wiki/X-Forwarded-For
	 * 
	 * @param xff
	 *            X-Forwarded-For头部的值
	 * @return 如果能够解析到client IP，则返回表示该IP的字符串，否则返回null
	 */
	private static String resolveClientIPFromXFF(String xff) {
		if (xff == null || xff.length() == 0) {
			return null;
		}
		String[] ss = xff.split(",");
		for (int i = ss.length - 1; i >= 0; i--) {// x-forward-for链反向遍历
			String ip = ss[i].trim();
			if (isValidIP(ip) && !isNativeIP(ip)) { // 判断ip是否合法，是否是公司机房ip
				return ip;
			}
		}

		// 如果反向遍历没有找到格式正确的外网IP，那就正向遍历找到第一个格式合法的IP
		for (int i = 0; i < ss.length; i++) {
			String ip = ss[i].trim();
			if (isValidIP(ip)) {
				return ip;
			}
		}
		return null;
	}

	/**
	 * 是否公司内部IP
	 * 
	 * @param ip
	 * @return
	 */
	private static boolean isNativeIP(String ip) {
		if (ip == null)
			return false;
		if (ip.startsWith("10.") || ip.startsWith("192.168.")) {
			return true;
		}
		return false;
	}

	private static boolean isValidIP(String ip) {
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip) || "127.0.0.1".equals(ip)) {
			return false;
		}
		return ipPattern.matcher(ip).matches();
	}

	public static boolean isPositiveNumbers(long... ls) {
		for (long l : ls) {
			if (l <= 0)
				return false;
		}
		return true;
	}

	public static String getLocalIp() {
		InetAddress ia = null;
		try {
			ia = InetAddress.getLocalHost();
			String localname = ia.getHostName();
			String localip = ia.getHostAddress();
			System.out.println("本机名称是：" + localname);
			System.out.println("本机的ip是 ：" + localip);
			return localip;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String checkLanguage(String lang) {
		Locale rtLang = HttpConstants.DEFAULT_LANGUAGE;
		if (StringUtils.isEmpty(lang)) {
			return rtLang.toString();
		}
		if (lang.toLowerCase().contains(HttpConstants.DEFAULT_LANGUAGE.getLanguage().toLowerCase())) {
			return rtLang.toString();
		}
		for (Locale loc : Locale.getAvailableLocales()) {
			if (lang.toLowerCase().contains(loc.getLanguage().toLowerCase())) {
				rtLang = loc;
				break;
			}
			if (lang.toLowerCase().contains(loc.getCountry().toLowerCase())) {
				rtLang = loc;
				break;
			}
		}
		return rtLang.toString();
	}

	public static String buildJSONResult(Object result) {
		if (result == null) {
			return null;
		}
		if (result instanceof String && ((String) result).startsWith("{")) {
			// 兼容 AutoLoadCommand 直接输出json
			return (String) result;
		}
		return gson.toJson(buildObjResult(result));

	}

	public static Object buildObjResult(Object result) {
		if (result == null) {
			return null;
		}
		if (result instanceof String && ((String) result).startsWith("{")) {
			// 兼容 AutoLoadCommand 直接输出json
			return result;
		}
		if (result instanceof Boolean) {
			// 输出形式的统一，0表示true(success),1表示false(failure)
			result = (Boolean) result ? 0 : 1;
		}
		if (result instanceof Integer || result instanceof Long || result instanceof String) {

			Map<String, Object> rtMap = new HashMap<String, Object>();
			rtMap.put("result", result);
			return rtMap;
		}
		return result;
	}

	// TODO
	// public static final String localIp =
	// XunleiEnvFinder.getIpcfg().getLocalIp();

	public static boolean isEnvTw06Pre() {
		// TODO
		// return "10.33.3.91".equals(localIp);
		return "10.33.3.91".equals("");
	}

	public static boolean isJsonString(Object o) {
		return String.class.isInstance(o) && ((String) o).startsWith("{");
	}

	public static boolean isHtmlString(Object o) {
		return String.class.isInstance(o) && ((String) o).startsWith("<");
	}
}
