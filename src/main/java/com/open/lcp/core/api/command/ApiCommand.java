package com.open.lcp.core.api.command;

import com.open.lcp.core.facade.ApiResult;

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
