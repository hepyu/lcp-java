package com.open.lcp.framework.core.api.command;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.open.lcp.framework.core.api.BlockChecker;
import com.open.lcp.framework.core.api.RequestChecker;
import com.open.lcp.framework.core.dao.entity.ApiMaxThreadsEntity;
import com.open.lcp.framework.core.facade.ApiResult;
import com.open.lcp.framework.core.facade.ApiResultCode;
import com.open.lcp.framework.core.service.ApiMaxThreadsService;
import com.open.lcp.framework.core.service.AppInitService;

public abstract class AbstractApiCommand implements ApiCommand {

	/**
	 * Logger for this class
	 */
	private static final Log logger = LogFactory.getLog(AbstractApiCommand.class);

	/** 当前已经阻塞数 */
	private static final Map<String, BlockChecker> mapBlockCheckers = new ConcurrentHashMap<String, BlockChecker>();

	/** 同一个 一分钟之内id的请求数量 */
	private static final Map<String, RequestChecker> mapRequestCheckers = new ConcurrentHashMap<String, RequestChecker>();

	public static final ApiResult LACKOF_REQUIRED_PARAM = new ApiResult(ApiResultCode.E_SYS_PARAM);
	public static final ApiResult SYS_UNKNOWN_METHOD = new ApiResult(ApiResultCode.E_SYS_UNKNOWN_METHOD);
	public static final ApiResult AR_USER_TOO_FAST = new ApiResult(ApiResultCode.E_USER_TOO_FAST);
	public static final ApiResult SYS_FAULT_ISOLATION = new ApiResult(ApiResultCode.E_SYS_FAULT_ISOLATION);
	// private static final String SERVICE = "qdmcp";
	//
	// private static final String BUSINESS_TYPE = "qdServer";

	private static final Log blockingLogger = LogFactory.getLog("mcpBlocks");

	// private static final Log statAccessLogger =
	// LogFactory.getLog("mcp_stat_access_log");

	// private static final Log statInterfaceLogger =
	// LogFactory.getLog("mcp_stat_interface_log");

	/** 阻塞数警戒线 */
	private static int BLOCK_THREADS = 160;
	private static int BLOCK_THREADS_WARN = 100;

	/** 一个用户每分钟请求数 */
	private static int requestNumPerMinutes = 240;

	/** 两次阻塞日志间的最小时间 */
	private static int blockLogPeriod = 60 * 1000;// 1分钟

	@Autowired
	private AppInitService appInitService;
	@Autowired
	private ApiMaxThreadsService apiMaxThreadsService;

	/**
	 * 子类不可重写
	 */
	@Override
	public final ApiResult execute(ApiCommandContext context) {
		ApiResult apiResult = null;
		String methodName = context.getMethodName();
		apiResult = this.beforeExecute(context);
		if (null == apiResult) {
			apiResult = checkRequestNumMethod(context);
			if (null == apiResult) {
				apiResult = checkBlockMethod(methodName, context);// 阻塞检测
			}
		}
		// apiResult = this.onExecute(context);
		try {
			this.afterExecute(context, apiResult);
		} catch (Exception e) {
			logger.error("stat log excetion", e);
		}
		return apiResult;
	}

	protected ApiResult beforeExecute(ApiCommandContext ctx) {
		final ApiFacadeMethod apiFacadeMethod = CommandModelHolder.getApiFacadeMethod(ctx.getMethodName(), ctx.getV());
		if (apiFacadeMethod.getLcpMethod() != null && apiFacadeMethod.getLcpMethod().loadAppInitData()//
				&& !StringUtils.isEmpty(ctx.getDeviceId())) {// McpMethod方式指定loadAppInitData，且deviceId有效
			ctx.setAppInitInfo(appInitService.getAppInitInfo(ctx.getDeviceId()));
		}
		return null;
	}

	protected void afterExecute(ApiCommandContext context, ApiResult apiResult) {
		// Map<String, String> stringParams = context.getStringParams();
		// // 调用接口时上传的参数转成JSON串
		// Map<String, Object> paramsMap = new HashMap<String, Object>();
		// for (String key : stringParams.keySet()) {
		// if (HttpConstants.platformParams.contains(key) ||
		// StatLogUtil.statParams.contains(key)) {
		// continue; // 跳过平台级参数和统计参数
		// }
		// String param = stringParams.get(key);
		// Object obj = ParamsUtils.fromJson(param);
		// paramsMap.put(key, obj);
		// }
	}

	public abstract ApiResult onExecute(ApiCommandContext context);

	/**
	 * 同一个id的请求数量
	 * 
	 * @param context
	 */
	private ApiResult checkRequestNumMethod(ApiCommandContext ctx) {
		final String userId = String.valueOf(ctx.getUserId());
		final String sig = ctx.getSig();
		final String methodName = ctx.getMethodName();
		if (!userId.equals("0")) {
			RequestChecker checker = mapRequestCheckers.get(userId);
			if (null == checker) {
				checker = new RequestChecker();
				mapRequestCheckers.put(userId, checker);
			}
			final int times = checker.incAndGet();
			if (times >= requestNumPerMinutes) {
				if (checker.getLastNotifyDist() > blockLogPeriod) {
					checker.notified();
					final String causeMessage = String.format(
							"%s has %s request sig[%s]\n" + //
									" Exception:userId: %s has %s request per minutes.method: %s", //
							userId, times, sig, //
							userId, requestNumPerMinutes, methodName);
					blockingLogger.warn(causeMessage);
				}
				return AR_USER_TOO_FAST;
			}
		}
		return null;
	}

	/**
	 * 检测阻塞方法
	 * 
	 * @param methodName
	 */
	private ApiResult checkBlockMethod(final String apiName, ApiCommandContext ctx) {
		final ApiFacadeMethod apiFacadeMethod = CommandModelHolder.getApiFacadeMethod(apiName, ctx.getV());
		if (apiFacadeMethod == null) {
			return SYS_UNKNOWN_METHOD;
		}
		final String nameAndVersion = String.format("%s:%s", apiName, apiFacadeMethod.getLcpMethod().ver());
		BlockChecker checker = mapBlockCheckers.get(nameAndVersion);
		if (checker == null) {
			checker = new BlockChecker();
			mapBlockCheckers.put(nameAndVersion, checker);
		}
		int maxThreads = BLOCK_THREADS;
		int warnThreads = BLOCK_THREADS_WARN;

		final ApiMaxThreadsEntity apiMaxThreads = apiMaxThreadsService.getMcpApiMaxThreads(nameAndVersion);
		if (apiMaxThreads != null) {
			maxThreads = apiMaxThreads.getMaxThreads();
			warnThreads = maxThreads * 3 / 4;
		}
		try {
			final int blocks = checker.blockCounter().incrementAndGet();
			if (blocks > maxThreads) {// 达到隔离线
				if (apiMaxThreads != null) {
					String jsonResult = apiMaxThreads.getOutResp();
					if (!jsonResult.isEmpty()) {
						String[] keys = apiMaxThreads.getKeys();
						if (keys != null && keys.length > 0) {
							for (String key : keys) {
								if (key.isEmpty()) {
									continue;
								}
								String[] ps = key.split("[:]", 2);
								final String pkey = ps[0];
								if (pkey.isEmpty()) {
									continue;
								}
								final String defaultValue = ps.length > 1 ? ps[1] : "";
								String value = ctx.getStringParams().get(pkey);
								if (value == null) {
									value = defaultValue;
								}
								jsonResult = StringUtils.replace(jsonResult, "{{" + pkey + "}}", value);
							}
						}
						if (checker.afterLastBlockTime() > blockLogPeriod) {// 通知超限
							checker.setLastBlockTime();
							final String causeMessage = String.format(
									"%s has %s threads, blocked and return default. sig[%s]", //
									nameAndVersion, blocks, ctx.getSig());
							blockingLogger.warn(causeMessage);
						}
						return new ApiResult(0, jsonResult);
					}
				}
				if (checker.afterLastBlockTime() > blockLogPeriod) {// 通知超限
					checker.setLastBlockTime();
					final String causeMessage = String.format("%s has %s threads, blocked. sig[%s]", //
							nameAndVersion, blocks, ctx.getSig());
					blockingLogger.error(causeMessage);
				}
				return SYS_FAULT_ISOLATION;
			}
			if (blocks > warnThreads) {// 达到隔离警告线
				if (checker.afterLastWarnTime() > blockLogPeriod) {// 通知超限
					checker.setLastWarnTime();
					final String causeMessage = String.format("%s had %s threads > %s, near to block limit %s. sig[%s]", //
							nameAndVersion, blocks, warnThreads, maxThreads, ctx.getSig());
					blockingLogger.warn(causeMessage);
				}
			}
			return this.onExecute(ctx);
		} finally {
			checker.blockCounter().decrementAndGet();
		}
	}

}