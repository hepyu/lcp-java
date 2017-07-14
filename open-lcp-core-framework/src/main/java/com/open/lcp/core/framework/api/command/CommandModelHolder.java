package com.open.lcp.core.framework.api.command;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.util.Assert;

import com.open.lcp.core.base.annotation.LcpHttpMethod;
import com.open.lcp.core.base.annotation.LcpHttpRequest;
import com.open.lcp.core.base.command.CommandContext;

/**
 * CommandModel加载的工具类，已经改为支持无参接口和仅一个参数的接口。
 * 
 * @author
 */
public class CommandModelHolder {
	private final static Map<Class<?>, List<ApiFacadeMethod>> methodModelMap = new ConcurrentHashMap<Class<?>, List<ApiFacadeMethod>>();
	private final static Map<String, Map<String, ApiFacadeMethod>> methodNameMap = new HashMap<String, Map<String, ApiFacadeMethod>>();

	public static List<ApiFacadeMethod> getApiFacadeMethodList() {
		final List<ApiFacadeMethod> afms = new ArrayList<ApiFacadeMethod>();
		for (Map<String, ApiFacadeMethod> afmMap : methodNameMap.values()) {
			for (ApiFacadeMethod afm : afmMap.values()) {
				afms.add(afm);
			}
		}
		return afms;
	}

	public static ApiFacadeMethod getApiFacadeMethod(String methodName, String version) {
		Map<String, ApiFacadeMethod> apis = methodNameMap.get(methodName);
		if (apis == null || apis.isEmpty()) {
			return null;
		}
		if (apis.size() == 1) {
			return apis.values().iterator().next();
		}
		if (version == null || version.isEmpty()) {
			version = "1.0";
		}
		final ApiFacadeMethod method = apis.get(version);
		if (method == null) {
			return apis.values().iterator().next();
		}
		return method;
	}

	/**
	 * 取方法级注解，支持无参接口和仅一个参数的接口。
	 * 
	 * @param o
	 * @return
	 */
	public static final List<ApiFacadeMethod> getMethodModelList(final Object o) {
		Class<?> clazz = o.getClass();
		List<ApiFacadeMethod> ls = methodModelMap.get(clazz);
		if (ls != null)
			return ls;
		ls = new ArrayList<ApiFacadeMethod>();
		while (true) {
			Method[] ms = clazz.getDeclaredMethods();
			for (Method m : ms) {
				LcpHttpMethod mcpMethod = m.getAnnotation(LcpHttpMethod.class);
				if (mcpMethod == null) {
					continue;
				}
				if (methodNameMap.containsKey(mcpMethod.name())
						&& methodNameMap.get(mcpMethod.name()).containsKey(mcpMethod.ver())) {
					final String errorMsg = String.format("ApiFacade load error, %s exist %s[%s/%s], exit."//
							, clazz.getName()//
							, m.getName()//
							, mcpMethod.name()//
							, mcpMethod.ver()//
					);
					Assert.notNull(null, errorMsg);
					// continue;
				}
				Class<?>[] ps = m.getParameterTypes();
				if (ps.length > 2) {// 超出两个入参一定是错的。最多只支持一个Req（对象或单值）和一个ctx
					throw new RuntimeException(String.format("McpMethod load error: %s ps more than 2 @%s.%s",
							ps.length, clazz.getName(), m.getName()));
				}

				if (ps.length == 2) {// 双参的第二个必须是ctx
					if (ps[1] != CommandContext.class && ps[1] != ApiCommandContext.class) {
						throw new RuntimeException(
								String.format("McpMethod load error: 2/%s ps is not CommandContext @%s.%s", ps.length,
										clazz.getName(), m.getName()));
					}
				}
				// if (ps[ps.length - 1] != CommandContext.class) {
				// final String errorMsg = String.format("ApiFacade load error:
				// 2nd params not CommandContext, %s %s[%s], exit."//
				// , clazz.getName()//
				// , m.getName()//
				// , mcpMethod.name()//
				// );
				// Assert.notNull(null, errorMsg);
				// continue;
				// }
				// if (m.getReturnType() == void.class) continue;//鐜板湪鏀寔void
				Class<?> req = null;/// 无参时，此数据为null
				LcpHttpRequest lcpReq = null;// 首参直接指定时，此值为非null
				Type lcpReqType = null;
				if (ps.length == 2 || (ps.length == 1 && !isCtx(ps[0]))) {
					req = ps[0];
					Annotation[][] ass = m.getParameterAnnotations();
					if (ass.length > 0) {
						Annotation[] as = ass[0];
						for (Annotation a : as) {
							if (LcpHttpRequest.class.isInstance(a)) {
								lcpReq = (LcpHttpRequest) a;
								lcpReqType = m.getGenericParameterTypes()[0];
								break;
							}
						}
					}
				}
				Class<?> resp = m.getReturnType();
				ApiFacadeMethod mm = ApiFacadeMethod.build(o, m, req, resp, mcpMethod);
				if (lcpReq != null && lcpReqType != null) {
					mm.setMcpReq(lcpReq);
					mm.setMcpReqType(lcpReqType);
				}
				ls.add(mm);
				Map<String, ApiFacadeMethod> apis = methodNameMap.get(mcpMethod.name());
				if (apis == null) {
					apis = new HashMap<String, ApiFacadeMethod>();
					methodNameMap.put(mcpMethod.name(), apis);
				}
				apis.put(mcpMethod.ver(), mm);
			}
			clazz = clazz.getSuperclass();
			if (clazz.getName().startsWith("java.")) {
				return ls;
			}
		}
	}

	public static boolean isCtx(Class<?> clazz) {
		return clazz == CommandContext.class || clazz == ApiCommandContext.class;
	}
}
