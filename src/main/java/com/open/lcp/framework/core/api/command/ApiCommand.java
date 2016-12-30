package com.open.lcp.framework.core.api.command;

import com.open.lcp.framework.core.facade.ApiResult;

/**
 * 命令接口
 */
public interface ApiCommand {

	/**
	 * Execute the command and return result
	 * 
	 * @param context
	 * @return
	 */
	ApiResult execute(ApiCommandContext context);

}
