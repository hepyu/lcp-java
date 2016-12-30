package com.open.lcp.framework.core.api;

import com.open.lcp.framework.core.api.command.CommandContext;

public class LcpThreadLocal {
	public static final ThreadLocal<CommandContext> thCommandContext = new ThreadLocal<CommandContext>();
}
