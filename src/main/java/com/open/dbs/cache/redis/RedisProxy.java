package com.open.dbs.cache.redis;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RedisProxy {
	private static final Log logger = LogFactory.getLog(RedisProxy.class);
	private static long warnTime = 100;
	// 维护一个目标对象
	private Object target;

	public RedisProxy(Object target) {
		this.target = target;
	}

	// 给目标对象生成代理对象
	public Object getProxyInstance() {
		return Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(),
				new InvocationHandler() {
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						// 执行目标对象方法
						long start = System.currentTimeMillis();
						Object returnValue = method.invoke(target, args);
						String key = "";
						if (args != null && args.length > 0) {
							if (args[0] instanceof String) {
								key = (String) args[0];
							} else {
								key = ((String[]) args[0])[0];
							}
							;
						}
						long costTime = System.currentTimeMillis() - start;
						RedisCounter.addCost(costTime);
						if (warnTime > 100) {
							logger.warn(
									"--redisProxy-methodName--" + method.getName() + "--key--" + key + "--" + costTime);
						}
						return returnValue;
					}
				});
	}
}
