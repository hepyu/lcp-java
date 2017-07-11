package com.open.lcp.passport.cache.impl;

import org.springframework.stereotype.Component;

import com.open.dbs.cache.redis.RedisX;
import com.open.dbs.cache.redis.RedisXFactory;
import com.open.lcp.ZKResourcePath;
import com.open.lcp.passport.MobileCodeType;
import com.open.lcp.passport.UserAccountType;
import com.open.lcp.passport.cache.CacheConstants;
import com.open.lcp.passport.cache.PassportCache;
import com.open.lcp.passport.service.dao.entity.PassportOAuthAccountEntity;
import com.open.lcp.passport.service.dao.entity.PassportUserAccountEntity;
import com.open.lcp.passport.ticket.Ticket;

@Component
public class PassportRedisCacheImpl implements PassportCache {

	private final RedisX cache = RedisXFactory.loadRedisX(ZKResourcePath.redis_lcp_passport);

	// 1.secretKey set and get

	@Override
	public Boolean setTicket(Ticket ticket, String t) {
		String key = CacheConstants.KEY_PRE_SECRETKEYCOUPLE + t;
		return cache.setRenewal(key, ticket, CacheConstants.EXPIRE_SECRETKEYCOUPLE,
				CacheConstants.RENEWAL_SECRETKEYCOUPLE) > 0;
	}

	@Override
	public Ticket getTicket(String t) {
		String key = CacheConstants.KEY_PRE_SECRETKEYCOUPLE + t;
		return cache.getRenewal(key, Ticket.class, CacheConstants.EXPIRE_SECRETKEYCOUPLE,
				CacheConstants.RENEWAL_SECRETKEYCOUPLE);
	}

	// 3.手机验证码
	@Override
	public Boolean setMobileCode(String mobile, String deviceId, int appId, MobileCodeType type, String mobileCode) {
		String key = CacheConstants.KEY_PRE_MOBILE_CODE + mobile + ":" + deviceId + ":" + appId + ":" + type.name()
				+ ":" + mobileCode;
		return cache.set(key, 0, CacheConstants.EXPIRE_MOBILE_CODE) > 0;
	}

	@Override
	public Boolean existMobileCode(String mobile, String deviceId, int appId, MobileCodeType type, String mobileCode) {
		String key = CacheConstants.KEY_PRE_MOBILE_CODE + mobile + ":" + deviceId + ":" + appId + ":" + type.name()
				+ ":" + mobileCode;
		Integer count = cache.get(key, Integer.class);
		if (count == null) {
			return false;
		} else {
			cache.set(key, count + 1);
			if (count >= 5) {
				cache.del(key);
				return false;
			} else {
				return true;
			}
		}
	}

	// 5.set or get userId by openId and type

	@Override
	public Boolean setUserId(String openId, UserAccountType type, Long userId) {
		String key = CacheConstants.KEY_PRE_XLUSERID_BY_OPENID_TYPE + openId + ":" + type.name();
		return cache.set(key, userId, CacheConstants.EXPIRE_XLUSERID_BY_OPENID_TYPE) > 0;
	}

	@Override
	public Long getUserId(String openId, UserAccountType type) {
		String key = CacheConstants.KEY_PRE_XLUSERID_BY_OPENID_TYPE + openId + ":" + type.name();
		return cache.get(key, Long.class);
	}

	@Override
	public Long delUserId(String openId, UserAccountType type) {
		String key = CacheConstants.KEY_PRE_XLUSERID_BY_OPENID_TYPE + openId + ":" + type.name();
		return cache.del(key);
	}

	// 7.set or get OAuthAccountInfo By UserIdAndType

	@Override
	public PassportOAuthAccountEntity getOAuthAccountInfoByUserIdAndType(Long userId, UserAccountType type) {
		String key = CacheConstants.KEY_PRE_PASSPORT_OAUTHACCOUNT_BY_XLUSERID_TYPE + userId + ":" + type.name();
		return cache.get(key, PassportOAuthAccountEntity.class);
	}

	@Override
	public Boolean setOAuthAccountInfoByUserIdAndType(Long userId, UserAccountType type,
			PassportOAuthAccountEntity passportOAuthAccountEntity) {
		String key = CacheConstants.KEY_PRE_PASSPORT_OAUTHACCOUNT_BY_XLUSERID_TYPE + userId + ":" + type.name();
		return cache.set(key, passportOAuthAccountEntity,
				CacheConstants.EXPIRE_PASSPORT_OAUTHACCOUNT_BY_XLUSERID_TYPE) > 0;
	}

	@Override
	public Long delOAuthAccountInfoByUserIdAndType(Long userId, UserAccountType type) {
		String key = CacheConstants.KEY_PRE_PASSPORT_OAUTHACCOUNT_BY_XLUSERID_TYPE + userId + ":" + type.name();
		return cache.del(key);
	}

	// 8.set or get PassportUserAccountEntity By userId

	@Override
	public PassportUserAccountEntity getUserInfoByUserId(Long userId) {
		String key = CacheConstants.KEY_PRE_PASSPORT_USERACCOUNT_BY_XLUSERID + userId;
		return cache.get(key, PassportUserAccountEntity.class);
	}

	@Override
	public Boolean setUserInfoByUserId(Long userId, PassportUserAccountEntity passportUserAccountEntity) {
		String key = CacheConstants.KEY_PRE_PASSPORT_USERACCOUNT_BY_XLUSERID + userId;
		return cache.set(key, passportUserAccountEntity, CacheConstants.EXPIRE_PASSPORT_USERACCOUNT_BY_XLUSERID) > 0;
	}

	@Override
	public Long delUserInfoByUserId(Long userId) {
		String key = CacheConstants.KEY_PRE_PASSPORT_USERACCOUNT_BY_XLUSERID + userId;
		return cache.del(key);
	}

}
