package com.open.lcp.biz.passport.service.cache;

import com.open.lcp.core.feature.user.api.MobileCodeType;

public interface MobileCodeCache {

	/**
	 * 
	 * ************************(1).KEY_PRE 定义*********************************
	 * 
	 */
	
	/**
	 * 存放手机手机验证码
	 * 
	 * key: pre+mobile+":"+deviceId+":"+ appId+ ":" + type + ":" + mobileCode;
	 * 
	 * value: mobileCode
	 */
	public static final String KEY_PRE_MOBILE_CODE = PassportCacheConstants.PRE + ":mobilecode:";
	
	/**
	 * 
	 * ************************(2).失效期定义*********************************
	 * 
	 */

	public static final int EXPIRE_MOBILE_CODE = 60 * 30;
	
	/**
	 * 
	 * ************************(3).方法*********************************
	 * 
	 */

	public Boolean setMobileCode(String mobile, String deviceId, int appId, MobileCodeType type, String mobileCode);

	public Boolean existMobileCode(String mobile, String deviceId, int appId, MobileCodeType type, String mobileCode);
}
