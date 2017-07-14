package com.open.lcp.core.framework.api.command;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.open.lcp.core.base.annotation.LcpHttpRequest;
import com.open.lcp.core.base.annotation.LcpParamRequired;
import com.open.lcp.core.base.command.CommandContext;
import com.open.lcp.core.base.facade.ApiResult;
import com.open.lcp.core.base.facade.ApiResultCode;
import com.open.lcp.core.framework.api.ApiException;
import com.open.lcp.core.framework.api.FieldLoadHolder;
import com.open.lcp.core.framework.api.ModelCastHolder;
import com.open.lcp.core.framework.api.RequiredCheck;
import com.open.lcp.core.framework.api.RequiredCheck.ErrorType;
import com.open.lcp.core.framework.api.listener.CommandListener;
import com.open.lcp.core.framework.util.LcpUtil;

/**
 * 指定一批Controller模式的指令，传统的command仅是自己的一个子集
 */
@Service
public class ApiFacadeCommand extends AbstractApiCommand implements InitializingBean {

	public static final ApiResult SYS_RPC_ERROR = new ApiResult(ApiResultCode.E_SYS_RPC_ERROR);
	public static final ApiResult API_RESULT_SUCCESS = new ApiResult(ApiResultCode.SUCCESS, 0);

	public static final ApiException EXP_ERR_REQ = new ApiException(ApiResultCode.E_SYS_PARAM);
	public static final ApiException EXP_ERR_RESP = new ApiException(ApiResultCode.E_SYS_RESP);

	private static final Map<String, Set<CommandListener>> listeners = new HashMap<String, Set<CommandListener>>();

	@Autowired(required = false)
	private List<CommandListener> commandListeners = null;

	/**
	 * Logger for this class
	 */
	private static final Log logger = LogFactory.getLog(ApiFacadeCommand.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mid.mcp.api.command.AbstractApiCommand#onExecute(com.mid.mcp.api.
	 * entity.ApiCommandContext)
	 */
	@Override
	public ApiResult onExecute(ApiCommandContext ctx) {
		if (logger.isDebugEnabled()) {
			logger.debug("onExecute(ApiCommandContext) - start");
		}
		final String methodName = ctx.getMethodName();
		final String version = ctx.getV();
		final ApiFacadeMethod apiFacadeMethod = CommandModelHolder.getApiFacadeMethod(methodName, version);
		final String sig = ctx.getSig() == null ? "null" : ctx.getSig();
		long execBeginTime = System.currentTimeMillis();
		try {
			this.beforeInvokeMethod(ctx);
			Object reqModel = null;
			try {
				if (apiFacadeMethod.getReqClass() == null) {// 无参，不用做参数的前期处理
				} else if (apiFacadeMethod.getLcpReq() != null) {// 参数级注解，基本数据解析
					reqModel = ModelCastHolder.mappingParameter(ctx.getStringParams(), apiFacadeMethod.getLcpReq(),
							apiFacadeMethod.getMcpReqType());
					if (reqModel == null) {
						reqModel = ModelCastHolder.mappingParameter(ctx.getBinaryParams(), apiFacadeMethod.getLcpReq(),
								apiFacadeMethod.getMcpReqType());
					}
				} else if (ctx.getBinaryParams() != null) {
					ModelCastHolder.mappingNew(ctx.getBinaryParams(), apiFacadeMethod.getReqClass());
					reqModel = ModelCastHolder.mapping(ctx.getStringParams(), reqModel);
				} else {
					reqModel = ModelCastHolder.mappingNew(ctx.getStringParams(), apiFacadeMethod.getReqClass());
				}
			} catch (Exception e) {
				logger.warn(String.format("onExecute(ApiCommandContext) reqModel build error, method:%s, sig:%s",
						methodName, sig), e);
				throw ApiException.E_SYS_INVALID_PARAM;
			}
			if (apiFacadeMethod.getReqClass() != null) {
				this.checkReqModel(reqModel, apiFacadeMethod.getLcpReq(), ctx);// 请求对象检查：参数必要性，值域
			}
			try {
				Object respModel = null;
				final Class<?>[] pts = apiFacadeMethod.getMethod().getParameterTypes();
				switch (pts.length) {
				case 0:// 完全无参
					respModel = apiFacadeMethod.getMethod().invoke(apiFacadeMethod.getMethodOfObject());
					break;
				case 1:// 仅一个参数，三种可能：Req对象/单值参/仅ctx
					if (reqModel != null) {// Req对象
						respModel = apiFacadeMethod.getMethod().invoke(apiFacadeMethod.getMethodOfObject(), reqModel);
					} else if (pts[0] == CommandContext.class) {// 仅ctx
						respModel = apiFacadeMethod.getMethod().invoke(apiFacadeMethod.getMethodOfObject(), ctx);
					} else {// 未知错误
						throw ApiException.E_SYS_INVALID_PARAM;
					}
					break;
				case 2:
					respModel = apiFacadeMethod.getMethod().invoke(apiFacadeMethod.getMethodOfObject(), reqModel, ctx);
					break;
				default:
				}
				// if (reqModel == null) {// 没参数
				// if (pts.length == 0) {// 完全无参
				//
				// } else if (apiFacadeMethod.getMcpReq() == null) {//
				// 仅有ApiCommandContext
				// respModel =
				// apiFacadeMethod.getMethod().invoke(apiFacadeMethod.getMethodOfObject(),
				// ctx);
				// } else {// 有参指令，无参数。是单独参数且未提供
				// if (apiFacadeMethod.getMcpReq().required()) {// 必选未提供时
				// throw ApiException.E_SYS_INVALID_PARAM;
				// }
				// respModel =
				// apiFacadeMethod.getMethod().invoke(apiFacadeMethod.getMethodOfObject(),
				// null, ctx);
				// }
				// } else {// 有参指令
				// respModel =
				// apiFacadeMethod.getMethod().invoke(apiFacadeMethod.getMethodOfObject(),
				// reqModel, ctx);
				// }
				final boolean isVoid = apiFacadeMethod.isVoidMethod();
				if (!isVoid) {// 无返回结果指令不用检查
					this.checkRespModel(respModel, ctx);
				}
				ApiResult result = null;
				if (isVoid) {// 无返回结果指令，无异常就是成功。
					result = API_RESULT_SUCCESS;
				} else {
					result = new ApiResult(ApiResultCode.SUCCESS, respModel);
				}
				if (logger.isDebugEnabled()) {
					logger.debug("onExecute(ApiCommandContext) - end");
				}
				this.afterInvokeMethod(ctx, execBeginTime, result);
				return result;
			} catch (InvocationTargetException e) {
				throw e.getCause();
			}
		} catch (ApiException e) {
			String errorMsg = e.getMessage();
			if (errorMsg == null) {
				if (logger.isDebugEnabled()) {
					final String rfLogError = "ApiException: userId[%s], sig[%s] errorCode[%s]";
					final String logError = String.format(rfLogError, ctx.getUserId(), ctx.getSig(), e.getErrorCode());
					logger.debug(logError, e);
				}
				ApiResult result = new ApiResult(e.getErrorCode());
				this.afterInvokeMethod(ctx, execBeginTime, result);
				return result;
			}
			ApiResult result = new ApiResult(e.getErrorCode(), errorMsg);
			if (e.getExt() != null) {
				result.setExt(e.getExt());
			}
			this.afterInvokeMethod(ctx, execBeginTime, result);
			return result;
		} catch (IllegalArgumentException e) {
			logger.error(
					String.format("onExecute(ApiCommandContext) IllegalArgument method:%s, sig:%s", methodName, sig),
					e);
		} catch (IllegalAccessException e) {
			logger.error(String.format("onExecute(ApiCommandContext) IllegalAccess method:%s, sig:%s", methodName, sig),
					e);
		} catch (InvocationTargetException e) {
			logger.error(
					String.format("onExecute(ApiCommandContext) InvocationTarget method:%s, sig:%s", methodName, sig),
					e);
		} catch (Throwable t) {
			logger.error(String.format("onExecute(ApiCommandContext) Throwable method:%s, sig:%s", methodName, sig), t);
		} finally {
			LcpUtil.rpcTimeCost(execBeginTime, ctx.getMethodName());
		}
		this.afterInvokeMethod(ctx, execBeginTime, SYS_RPC_ERROR);
		return SYS_RPC_ERROR;
	}

	private void beforeInvokeMethod(CommandContext ctx) {
		final String cmdName = ctx.getMethodName();
		Set<CommandListener> thisListener = listeners.get(cmdName.toLowerCase());
		if (thisListener != null) {
			for (CommandListener c : thisListener) {
				try {
					c.beforeExec(ctx);
					if (logger.isDebugEnabled()) {
						logger.debug("ApiFacadeCommand " + cmdName + "'s listener " + c.getClass().getName()
								+ ".beforeExec invoked.");
					}
				} catch (ApiException e) {
					throw e;
				} catch (Throwable t) {
					logger.warn("beforeInvokeMethod(ApiCommandContext) - exception ignored", t); //$NON-NLS-1$
				}
			}
		}
	}

	private void afterInvokeMethod(CommandContext ctx, long execBeginTime, ApiResult result) {
		final String cmdName = ctx.getMethodName();
		Set<CommandListener> thisListener = listeners.get(cmdName.toLowerCase());
		if (thisListener != null) {
			for (CommandListener c : thisListener) {
				try {
					if (result == null) {
						c.afterExec(ctx, execBeginTime, -1, null, null);
						if (logger.isDebugEnabled()) {
							logger.debug("ApiFacadeCommand " + cmdName + "'s listener " + c.getClass().getName()
									+ ".afterExec null result invoked.");
						}
					} else {
						c.afterExec(ctx, execBeginTime, result.getCode(), result.getData(), result.getExt());
						if (logger.isDebugEnabled()) {
							logger.debug("ApiFacadeCommand " + cmdName + "'s listener " + c.getClass().getName()
									+ ".afterExec invoked.");
						}
					}
				} catch (Throwable t) {
					logger.warn("afterInvokeMethod(ApiCommandContext) - exception ignored", t); //$NON-NLS-1$
				}
			}
		}
	}

	private static final String SF_REQ_ERROR = "checkReqModel: userId[%s], sig[%s], %s: %s [%s]";

	/**
	 * 对请求对象的自定义检查
	 * 
	 * @param reqModel
	 * @param reqParameter
	 * @param ctx
	 * @throws ApiException
	 */
	protected void checkReqModel(Object reqModel, LcpHttpRequest lcpReq, ApiCommandContext ctx) throws ApiException {
		RequiredCheck result = null;
		if (lcpReq != null) {// 按单个参数加注解的形式来处理
			result = LcpRequiredCheckHolder.check(lcpReq, reqModel);
		} else {
			result = LcpRequiredCheckHolder.checkMonolayer(reqModel);
		}
		if (result != null && result.getErrorType() != ErrorType.Pass) {
			logger.warn(String.format(SF_REQ_ERROR, ctx.getUserId(), ctx.getSig(), ctx.getMethodName(),
					result.toMessage(), FieldLoadHolder.toString(result.getValue())));
			LcpParamRequired required = result.getRequired();
			if (required != null && required.errorCode() > 0) {
				int errorCode = required.errorCode();
				String errorMsg = required.errorMsg();
				if (errorMsg == null || errorMsg.isEmpty()) {
					throw new ApiException(errorCode);
				}
				throw new ApiException(errorCode, errorMsg);
			}
			if (lcpReq != null && lcpReq.errorCode() > 0) {
				int errorCode = lcpReq.errorCode();
				String errorMsg = lcpReq.errorMsg();
				if (errorMsg == null || errorMsg.isEmpty()) {
					throw new ApiException(errorCode);
				}
				throw new ApiException(errorCode, errorMsg);
			}
			throw EXP_ERR_REQ;
		}
	}

	private static final String SF_RESP_ERROR = "checkRespModel: userId[%s], sig[%s], %s: %s [%s]";

	/**
	 * 对结果对象的自定义检查
	 * 
	 * @param respModel
	 * @param context
	 * @throws ApiException
	 */
	protected void checkRespModel(Object respModel, ApiCommandContext ctx) throws ApiException {
		RequiredCheck result = LcpRequiredCheckHolder.checkMultilayer(respModel);
		if (result != null && result.getErrorType() != ErrorType.Pass) {
			logger.warn(String.format(SF_RESP_ERROR//
					, ctx.getUserId()//
					, ctx.getSig()//
					, ctx.getMethodName()//
					, result.toMessage()//
					, FieldLoadHolder.toString(result.getValue())));
			throw EXP_ERR_RESP;
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (commandListeners == null || commandListeners.isEmpty()) {
			logger.warn("ApiFacadeCommand no command lisener found!");
			return;
		}
		for (CommandListener c : commandListeners) {
			String[] cs = c.getCommands();
			if (cs == null || cs.length == 0) {
				logger.error("ApiFacadeCommand no commandname found @ " + c.getClass().getName());
				continue;
			}
			for (String commandName : cs) {
				if (commandName == null || (commandName = commandName.toLowerCase().trim()).isEmpty()) {
					logger.error("ApiFacadeCommand empty commandname found @ " + c.getClass().getName());
					continue;
				}
				Set<CommandListener> setLisener = listeners.get(commandName);
				if (setLisener == null) {
					setLisener = new HashSet<CommandListener>();
					listeners.put(commandName, setLisener);
				}
				setLisener.add(c);
				logger.info("ApiFacadeCommand " + commandName + " lisener found @ " + c.getClass().getName());
			}
		}
	}
}
