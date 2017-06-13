package com.open.lcp.framework.core.api.command;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.util.Assert;

import com.open.lcp.framework.core.annotation.LcpMethod;
import com.open.lcp.framework.core.annotation.LcpReq;

/**
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
	 * 鍙栨柟娉曠骇娉ㄨВ锛屾敮鎸佹棤鍙傛帴鍙ｅ拰浠呬竴涓弬鏁扮殑鎺ュ彛銆�
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
				LcpMethod mcpMethod = m.getAnnotation(LcpMethod.class);
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
				if (ps.length > 2) {// 瓒呭嚭涓や釜鍏ュ弬涓�瀹氭槸閿欑殑銆傛渶澶氬彧鏀寔涓�涓猂eq锛堝璞℃垨鍗曞�硷級鍜屼竴涓猚tx
					throw new RuntimeException(String.format("McpMethod load error: %s ps more than 2 @%s.%s",
							ps.length, clazz.getName(), m.getName()));
				}

				if (ps.length == 2) {// 鍙屽弬鐨勭浜屼釜蹇呴』鏄痗tx
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
				Class<?> req = null;// 鏃犲弬鏃讹紝姝ゆ暟鎹负null
				LcpReq lcpReq = null;// 棣栧弬鐩存帴鎸囧畾鏃讹紝姝ゅ�间负闈瀗ull
				Type lcpReqType = null;
				if (ps.length == 2 || (ps.length == 1 && !isCtx(ps[0]))) {
					req = ps[0];
					Annotation[][] ass = m.getParameterAnnotations();
					if (ass.length > 0) {
						Annotation[] as = ass[0];
						for (Annotation a : as) {
							if (LcpReq.class.isInstance(a)) {
								lcpReq = (LcpReq) a;
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
