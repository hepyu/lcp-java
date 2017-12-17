package com.open.lcp.core.framework.api.command;

import com.open.lcp.core.api.facade.ApiResult;

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
