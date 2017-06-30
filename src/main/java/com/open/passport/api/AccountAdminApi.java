package com.open.passport.api;

/**
 * 此处定义的接口只允许admin后台调用
 * 
 * @author hpy
 * 
 */
// TODO 以后可能会加只允许admin后台调用的code限制
public interface AccountAdminApi {

	@Deprecated
	public boolean registUser(String userName, String nickName, String gender, String avatar, String desc,
			String userAccountType);
}
