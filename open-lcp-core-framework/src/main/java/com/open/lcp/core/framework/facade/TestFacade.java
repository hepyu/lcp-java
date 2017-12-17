package com.open.lcp.core.framework.facade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.open.lcp.core.api.annotation.LcpHttpMethod;
import com.open.lcp.core.api.command.CommandContext;
import com.open.lcp.core.api.facade.ApiFacade;

@Component
public class TestFacade implements ApiFacade {

	@LcpHttpMethod(name = "helloworld", ver = "1.0", desc = "helloworld")
	public String helloworld(CommandContext context) {
		return "helloworld";
	}

	@LcpHttpMethod(name = "test.return.map", ver = "1.0", desc = "test.return.map")
	public Map<String, String> testReturnMap(CommandContext context) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("test.return.map", "ok");
		return map;
	}

	@LcpHttpMethod(name = "test.return.string", ver = "1.0", desc = "test.return.string")
	public String testReturnString(CommandContext context) {
		return "test.return.string";
	}

	@LcpHttpMethod(name = "test.return.list", ver = "1.0", desc = "test.return.list")
	public List<String> testReturnList(CommandContext context) {

		List<String> list = new ArrayList<String>();
		list.add("e1");
		list.add("e2");
		list.add("e3");
		list.add("e4");
		list.add("e5");
		return list;
	}

	@LcpHttpMethod(name = "test.return.array", ver = "1.0", desc = "test.return.array")
	public String[] testReturnArray(CommandContext context) {
		String[] array = new String[] { "e1", "e2", "e3", "e4", "e5" };
		return array;
	}
}
