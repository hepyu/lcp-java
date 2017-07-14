package com.open.lcp.core.framework.api;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.GZIPOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.ModelAndView;
import com.google.gson.Gson;
import com.open.lcp.core.base.annotation.LcpHttpMethod;
import com.open.lcp.core.base.command.CommandContext;
import com.open.lcp.core.base.facade.ApiResult;
import com.open.lcp.core.framework.api.command.ApiCommandContext;
import com.open.lcp.core.framework.api.command.ApiFacadeMethod;
import com.open.lcp.core.framework.api.command.CommandModelHolder;
import com.open.lcp.core.framework.api.service.ApiExceptionMessageService;
import com.open.lcp.core.framework.api.service.dao.IgnoreMethodLogDAO;
import com.open.lcp.core.framework.api.service.dao.entity.IgnoreMethodLogEntity;
import com.open.lcp.core.framework.consts.LcpConstants;
import com.open.lcp.core.framework.loader.TimerLoader;
import com.open.lcp.core.framework.util.LcpUtil;

@Component
public class ApiReturnValueHandler implements HandlerMethodReturnValueHandler, TimerLoader {
	private static final Log logReqResp = LogFactory.getLog("mcp_req_resp");

	//private static final String env_host = EnvFinderUtil.getIpcfg().getLocalIp();
	private static final String env_host = null;

	/* 返回结果有可能很大，且统计的童鞋不关心的 */
	private Set<String> sysConfigIgnoreLogMethodSet;

	@Autowired
	private IgnoreMethodLogDAO lcpIgnoreMethodLogDAO;

	@Autowired
	private ApiExceptionMessageService apiExceptionMessageService;

	private static final Gson gsonDefault = LcpConstants.gson;

	private static final Gson gsonL2S = LcpConstants.gsonL2S;

	private static WeakReference<ConcurrentHashMap<Object, String>> Obj2JsonCache = new WeakReference<ConcurrentHashMap<Object, String>>(new ConcurrentHashMap<Object, String>());

	@Override
	public boolean supportsReturnType(MethodParameter returnType) {
		final boolean isSupport = ApiResult.class.equals(returnType.getParameterType());
		if (isSupport)
			return true;
		return ModelAndView.class.equals(returnType.getParameterType());
	}

	private static final byte[] RESP_EMPT = "what are you want?".getBytes();
	private static final String RESP_ERROR_JSON = gsonDefault.toJson(new ApiResult(1, "system error"));
	private static final byte[] RESP_ERROR = RESP_ERROR_JSON.getBytes();

	@Override
	public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws IOException {
		mavContainer.setRequestHandled(true);
		final HttpServletResponse resp = webRequest.getNativeResponse(HttpServletResponse.class);
		if (returnValue == null || !ApiResult.class.isInstance(returnValue)) {
			resp.setStatus(200);
			resp.setContentType("text/plain;charset=UTF-8");
			if (ModelAndView.class.equals(returnType.getParameterType())) {
			} else {
				resp.getOutputStream().write(RESP_EMPT);
			}
			return;
		}
		final ApiResult apiResult = (ApiResult) returnValue;
		final HttpServletRequest req = webRequest.getNativeRequest(HttpServletRequest.class);
		final boolean isGZ = "1".equals(req.getParameter(LcpConstants.PARAM_GZ));
		final String jsCall = req.getParameter(LcpConstants.PARAM_JS_CALL);
		final String version = req.getParameter(LcpConstants.PARAM_V);

		final boolean isLong2String = "1".equals(req.getParameter(LcpConstants.PARAM_LONG_2_STRING));
		final Gson gson = isLong2String ? gsonL2S : gsonDefault;
		// final String sig = req.getParameter(LcpConstants.PARAM_SIG);
		resp.setStatus(HttpServletResponse.SC_OK);
		if (env_host != null) {
			resp.addHeader("ViaS", env_host);
		}
		final String methodName = LcpUtil.getCmdMethodFromURI(req.getRequestURI());
		boolean cacheResult = false;
		if (methodName != null && !methodName.isEmpty()) {
			final ApiFacadeMethod apiFacadeMethod = CommandModelHolder.getApiFacadeMethod(methodName, version);
			if (apiFacadeMethod != null) {
				final LcpHttpMethod mcpMethod = apiFacadeMethod.getLcpMethod();
				if (mcpMethod != null && mcpMethod.cacheResult()) {// 确认打开了cacheResult
					cacheResult = true;
				}
			}
		}
		OutputStream os = null;
		try {
			final String resultJson;
			if (apiResult.getCode() == 0) {
				final String cachedJson = cacheResult ? getJsonFromCache(apiResult.getData()) : null;
				if (cachedJson != null) {
					resultJson = cachedJson;
				} else {
					final Object resultO = buildObjResult(apiResult.getData());
					if (LcpUtil.isJsonString(resultO) || LcpUtil.isHtmlString(resultO)) {
						resultJson = (String) resultO;
					} else {
						resultJson = gson.toJson(resultO);
					}
					if (cacheResult) {
						setJsonToCache(apiResult.getData(), resultJson);
					}
				}
			} else {
				if (apiResult.getData() == null) {
					String message = apiExceptionMessageService.getMessage(apiResult.getCode());
					if (message != null) {
						apiResult.setData(message);
					}
				}
				resultJson = gson.toJson(buildObjResult(apiResult));
			}
			if (isGZ) {
				resp.setContentType("application/json-gz");
				resp.setHeader("content-encoding", "gzip");
				os = new GZIPOutputStream(resp.getOutputStream());
			} else {
				if (jsCall != null && jsCall.length() > 0) {
					resp.setContentType("text/javascript;charset=UTF-8");
				} else if (LcpUtil.isJsonString(resultJson)) {
					resp.setContentType("text/plain;charset=UTF-8");
				} else if (LcpUtil.isHtmlString(resultJson)) {
					resp.setContentType("text/html;charset=UTF-8");
				} else {
					resp.setContentType("text/plain;charset=UTF-8");
				}
				os = resp.getOutputStream();
			}
			final String apiName = (String) req.getAttribute(LcpConstants.REQ_API_METHOD_NAME);
			final Object psMap = req.getAttribute(LcpConstants.REQ_ATTR_PerSMAP);
			final String reqJson = gson.toJson(psMap);
			Object statExt = null;
			{
				final CommandContext cctx = LcpThreadLocal.thCommandContext.get();
				if (cctx != null) {
					cctx.addStatExt("rtncode", apiResult.getCode());
					if (ApiCommandContext.class.isInstance(cctx)) {
						ApiCommandContext acc = (ApiCommandContext) cctx;
						statExt = acc.getStatExt();
					}
				} else {
					statExt = new Rtn(apiResult.getCode());
				}
			}
			if (resultJson != null) {
				if (jsCall != null && jsCall.length() > 0) {
					final String resultJsCall = String.format("%s(%s);", jsCall, resultJson);
					os.write(resultJsCall.getBytes("UTF-8"));
				} else {
					// if (XunleiEnvFinder.ENV_T16.equals(XunleiEnvFinder.getProfile()) && resultJson.charAt(resultJson.length() - 1) == '}') {// 确认是json，拼入sig值。
					// resultJson = String.format("%s,\"mcp_sig\":\"%s\"}", resultJson.substring(0, resultJson.length() - 1), sig);
					// }
					os.write(resultJson.getBytes("UTF-8"));
				}
				logstat(apiName, reqJson, resultJson, statExt);
			} else {
				os.write(RESP_ERROR);
				logstat(apiName, reqJson, RESP_ERROR_JSON, statExt);
			}
			os.flush();
		} finally {
			LcpThreadLocal.thCommandContext.set(null);
			if (os != null) {
				try {
					os.close();
				} catch (Exception e) {

				}
			}
		}

	}

	private static String getJsonFromCache(Object apiResultData) {
		ConcurrentHashMap<Object, String> map = Obj2JsonCache.get();
		if (map == null) {
			return null;
		}
		return map.get(apiResultData);
	}

	private static void setJsonToCache(Object apiResultData, String json) {
		ConcurrentHashMap<Object, String> map = Obj2JsonCache.get();
		if (map == null) {
			map = new ConcurrentHashMap<Object, String>();
			Obj2JsonCache = new WeakReference<ConcurrentHashMap<Object, String>>(map);
		}
		map.putIfAbsent(apiResultData, json);
	}

	private static final String RF_STAT_LOG = "{\"api\":\"%s\",\"time\":%s,\"req\":%s,\"resp\":%s,\"ext\":%s}";

	/**
	 * 记录统计日志
	 * 
	 * @param apiName
	 * @param reqJson
	 * @param resultJson
	 */
	private final void logstat(String apiName, String reqJson, String resultJson, Object statExt) {
		if (apiName == null || apiName.isEmpty()) {// 忽略null和空的请求日志
			return;
		}
		final String extJson = statExt == null ? "{}" : gsonDefault.toJson(statExt);
		final long time = System.currentTimeMillis();
		if (sysConfigIgnoreLogMethodSet.contains(apiName)) {// 需忽略返回值的，只记录其大小
			logReqResp.info(String.format(RF_STAT_LOG, apiName, time, reqJson, resultJson.length(), extJson));
		} else {
			logReqResp.info(String.format(RF_STAT_LOG, apiName, time, reqJson, resultJson, extJson));
		}
	}

	public static Object buildObjResult(Object result) {
		if (result == null) {
			return null;
		}
		if (LcpUtil.isJsonString(result)) {
			return result;
		}
		if (LcpUtil.isHtmlString(result)) {
			return result;
		}
		if (result instanceof Boolean) {
			result = (Boolean) result ? 0 : 1;
		}
		if (result instanceof Integer || result instanceof Long || result instanceof String) {
			Map<String, Object> rtMap = new HashMap<String, Object>();
			rtMap.put("result", result);
			return rtMap;
		}
		return result;
	}

	@Override
	public boolean initLoad() {
		return true;
	}

	@Override
	public boolean reloadable(int hour, int minute, long minuteOfAll) {
		return minute % 10 == 0;
	}

	@Override
	public String reload() {
		Set<String> tempSet = new HashSet<String>();
		List<IgnoreMethodLogEntity> list = lcpIgnoreMethodLogDAO.listAll();
		for (IgnoreMethodLogEntity o : list) {
			tempSet.add(o.getMethodName());
		}

		sysConfigIgnoreLogMethodSet = tempSet;

		return "OK";
	}

	public static class Rtn {
		private Rtn(int rtncode) {
			this.rtncode = rtncode;
		}

		private final int rtncode;

		public int getRtncode() {
			return rtncode;
		}
	}
}
