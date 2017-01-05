package com.open.lcp.biz.facade;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.open.lcp.framework.core.annotation.LcpMethod;
import com.open.lcp.framework.core.api.command.CommandContext;
import com.open.lcp.framework.core.facade.ApiFacade;

@Component
public class HelloWorldFacade implements ApiFacade {

	@LcpMethod(name = "test.return.map", ver = "1.0", desc = "test.return.map")
	public Map<String,String> testReturnMap(CommandContext
			context) {
		Map<String, String> map = new HashMap<String,String>();
		map.put("test.return.map", "ok");
		return map;
	}
}
