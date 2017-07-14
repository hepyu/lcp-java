package com.open.lcp.core.framework.api.command;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import com.open.lcp.core.base.annotation.LcpHttpMethod;
import com.open.lcp.core.base.annotation.LcpHttpRequest;

/**
 * 方法接口定义
 * 
 * @author hepengyuan
 */
public class ApiFacadeMethod {

	private ApiFacadeMethod() {
	}

	public static ApiFacadeMethod build(Object o, Method method, Class<?> reqClass, Class<?> respClass,
			LcpHttpMethod lcpMethod) {
		ApiFacadeMethod mm = new ApiFacadeMethod();
		mm.methodOfObject = o;
		mm.method = method;
		mm.reqClass = reqClass;
		mm.respClass = respClass;
		mm.lcpMethod = lcpMethod;

		mm.isVoidMethod = mm.respClass == Void.TYPE;

		return mm;
	}

	private Object methodOfObject;

	private Method method;

	// private String methodName;
	//
	// private String ver;
	//
	// private String desc;

	private LcpHttpMethod lcpMethod;

	private Class<?> reqClass;

	private Class<?> respClass;

	private Type mcpReqType;

	private LcpHttpRequest lcpReq;

	private transient boolean isVoidMethod;

	public Class<?> getReqClass() {
		return reqClass;
	}

	public Class<?> getRespClass() {
		return respClass;
	}

	public LcpHttpMethod getLcpMethod() {
		return lcpMethod;
	}

	public Object getMethodOfObject() {
		return methodOfObject;
	}

	public Method getMethod() {
		return method;
	}

	public Type getMcpReqType() {
		return mcpReqType;
	}

	public boolean isVoidMethod() {
		return isVoidMethod;
	}

	public void setMcpReqType(Type mcpReqType) {
		this.mcpReqType = mcpReqType;
	}

	public LcpHttpRequest getLcpReq() {
		return lcpReq;
	}

	public void setMcpReq(LcpHttpRequest lcpReq) {
		this.lcpReq = lcpReq;
	}

	@Override
	public String toString() {
		return "ApiFacadeMethod [methodOfObject=" + methodOfObject + ", method=" + method + ", lcpMethod=" + lcpMethod
				+ ", reqClass=" + reqClass + ", respClass=" + respClass + ", mcpReqType=" + mcpReqType + ", lcpReq="
				+ lcpReq + "]";
	}

}
