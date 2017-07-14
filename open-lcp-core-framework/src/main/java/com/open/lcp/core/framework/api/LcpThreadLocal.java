package com.open.lcp.core.framework.api;

import com.open.lcp.core.framework.api.command.CommandContext;

public class LcpThreadLocal {
	public static final ThreadLocal<CommandContext> thCommandContext = new ThreadLocal<CommandContext>();
}
