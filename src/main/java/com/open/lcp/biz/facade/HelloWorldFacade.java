package com.open.lcp.biz.facade;

import org.springframework.stereotype.Component;

import com.open.lcp.framework.core.annotation.LcpMethod;
import com.open.lcp.framework.core.facade.ApiFacade;

@Component
public class HelloWorldFacade implements ApiFacade {

	@LcpMethod(name = "hello.world", ver = "1.0", desc = "hello world")
	public String helloWorld() {
		return "hello world";
	}
}
