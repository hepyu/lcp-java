package com.open.lcp.passport.cache;

/**
 * @author hpy
 *
 */
/**
 * @author hpy
 *
 */
/**
 * @author hpy
 * 
 */
public class CacheConstants {
	
	
	public static final String YOULIAO_PASSPORT_REDIS_SERVICE_NAME="passport";
	
	public static final String YOULIAO_PASSPORT_USER_KIND_REDIS_SERVICE_NAME="commentListRedis";

	public static final String PRE = "4passport";

	/**
	 * 
	 * ************************(1).KEY_PRE 定义*********************************
	 * 
	 */

	/**
	 * 存放每个用户终端的唯一标识(一对sk,uk)
	 * 
	 * key: pre + t
	 * 
	 * value: com.xunlei.xlmc.passport.component.ticket.Ticket实例
	 */
	public static final String KEY_PRE_SECRETKEYCOUPLE = PRE
			+ ":secretkeycouple:";

	/**
	 * 存放同一个用户的多个t票
	 * 
	 * 暂时不用
	 * 
	 */
	public static final String KEY_PRE_PASSPORTUSERID_DEVICES = PRE
			+ ":appsPerUser:";

	/**
	 * 存放手机手机验证码
	 * 
	 * key: pre+mobile+":"+deviceId+":"+ appId+ ":" + type + ":" + mobileCode;
	 * 
	 * value: mobileCode
	 */
	public static final String KEY_PRE_MOBILE_CODE = PRE + ":mobilecode:";

	/**
	 * 存放userIdentify。迅雷账号系统会来这里验证.
	 * 
	 * key: pre + userIdentify
	 * 
	 * value: userIdentify
	 */
	public static final String KEY_PRE_PASSPORT_USER_ID_CHECK = PRE
			+ ":puserid:";

	/**
	 * 存放xluserid.
	 * 
	 * key: pre + openId + ":" + type
	 * 
	 * value:xluserid
	 */
	public static final String KEY_PRE_XLUSERID_BY_OPENID_TYPE = PRE
			+ ":xluserid:byOpenidType:";

	// 6.set or get passportUserAccount by passportUserId

	/**
	 * 根据passportUserId存放PassportUserAccount对象.
	 * 
	 * key:pre +　passportUserId
	 * 
	 * value:PassportUserAccount对象
	 * 
	 */
	public static final String KEY_PRE_PASSPORT_USERACCOUNT_BY_PASSPORT_USERID = PRE
			+ ":useraccount:bypid:";

	/**
	 * 根据xluserId, type存放PassportOAuthAccount对象
	 * 
	 * key: pre + xluserid +　type
	 * 
	 * value: PassportOAuthAccountEntity
	 */
	public static final String KEY_PRE_PASSPORT_OAUTHACCOUNT_BY_XLUSERID_TYPE = PRE
			+ ":oauthaccount:byXluseridType:";

	/**
	 * 根据xlUserId获取PassportUserAccount对象
	 * 
	 * key: pre + xluserId
	 * 
	 * value: PassportUserAccountEntity
	 */
	public static final String KEY_PRE_PASSPORT_USERACCOUNT_BY_XLUSERID = PRE
			+ ":useraccount:byxluserid:";

	/**
	 * 存放所有特殊账号的list
	 */
	public static final String KEY_PRE_PASSPORT_SPECIALACCOUNT_ALL = PRE
			+ ":specialaccount:list";
	
	/**
	 * 存放sha1
	 * 
	 * key: pre + avatarSHA1
	 */
	public static final String KEY_PRE_AVATAR_SHA1 = PRE + ":avatar:sha1:";

	public static final String KEY_PRE_PASSPORT_SESSIONID_PREFIX=PRE + ":sessionid:xlt:%s";
	
	/**
	 * 
	 * ************************(2).失效期定义*********************************
	 * 
	 */

	public static final int EXPIRE_SECRETKEYCOUPLE = 3600 * 24 * 30;

	public static final int EXPIRE_PASSPORTUSERID_DEVICES = 3600;

	public static final int EXPIRE_MOBILE_CODE = 60 * 30;

	public static final int EXPIRE_PASSPORT_USER_ID_CHECK = 86400;

	public static final int EXPIRE_XLUSERID_BY_OPENID_TYPE = 86400;

	public static final int EXPIRE_PASSPORT_USERACCOUNT_BY_PASSPORT_USERID = 86400;

	public static final int EXPIRE_PASSPORT_OAUTHACCOUNT_BY_XLUSERID_TYPE = 86400;

	public static final int EXPIRE_PASSPORT_USERACCOUNT_BY_XLUSERID = 86400;

	public static final int EXPIRE_PASSPORT_SPECIALACCOUNT_ALL = 86400;
	
	public static final int EXPIRE_PRE_AVATAR_SHA1 = 86400;
	
	public static final int EXPIRE_PASSPORT_SESSENID=7*3600;// 7天 有效期sessionId的过期时间那边 web最长7天 客户端最长13天

	/**
	 * 
	 * ************************(3).租期定义*********************************
	 * 
	 */

	public static final int RENEWAL_SECRETKEYCOUPLE = 3600 * 24 * 45;

}
