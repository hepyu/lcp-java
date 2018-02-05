package com.open.lcp.core.feature.user.service.cache.impl;

import com.open.lcp.core.env.LcpResource;
import com.open.lcp.core.feature.user.api.UserType;
import com.open.lcp.core.feature.user.service.cache.CacheConstants;
import com.open.lcp.core.feature.user.service.cache.UserCache;
import com.open.lcp.core.feature.user.service.dao.entity.UserOAuthInfoEntity;
import com.open.lcp.core.feature.user.ticket.Ticket;
import com.open.lcp.core.feature.user.service.dao.entity.UserDetailInfoEntity;
import com.open.lcp.dbs.cache.ssdb.SSDBLoader;
import com.open.lcp.dbs.cache.ssdb.SSDBX;

@Deprecated
// @Component
public class UserSSDBCacheImpl implements UserCache {

	private final SSDBX cache = SSDBLoader.loadSSDBX(LcpResource.lcp_ssdb_core_feature_user);

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

	// 5.set or get userId by openId and type

	@Override
	public Boolean setUserId(String openId, UserType type, Long userId) {
		String key = CacheConstants.KEY_PRE_XLUSERID_BY_OPENID_TYPE + openId + ":" + type.name();
		return cache.set(key, userId, CacheConstants.EXPIRE_XLUSERID_BY_OPENID_TYPE) > 0;
	}

	@Override
	public Long getUserId(String openId, UserType type) {
		String key = CacheConstants.KEY_PRE_XLUSERID_BY_OPENID_TYPE + openId + ":" + type.name();
		return cache.get(key, Long.class);
	}

	@Override
	public Long delUserId(String openId, UserType type) {
		String key = CacheConstants.KEY_PRE_XLUSERID_BY_OPENID_TYPE + openId + ":" + type.name();
		return cache.del(key);
	}

	// 7.set or get OAuthAccountInfo By UserIdAndType

	@Override
	public UserOAuthInfoEntity getOAuthAccountInfoByUserIdAndType(Long userId, UserType type) {
		String key = CacheConstants.KEY_PRE_PASSPORT_OAUTHACCOUNT_BY_XLUSERID_TYPE + userId + ":" + type.name();
		return cache.get(key, UserOAuthInfoEntity.class);
	}

	@Override
	public Boolean setOAuthAccountInfoByUserIdAndType(Long userId, UserType type,
			UserOAuthInfoEntity passportOAuthAccountEntity) {
		String key = CacheConstants.KEY_PRE_PASSPORT_OAUTHACCOUNT_BY_XLUSERID_TYPE + userId + ":" + type.name();
		return cache.set(key, passportOAuthAccountEntity,
				CacheConstants.EXPIRE_PASSPORT_OAUTHACCOUNT_BY_XLUSERID_TYPE) > 0;
	}

	@Override
	public Long delOAuthAccountInfoByUserIdAndType(Long userId, UserType type) {
		String key = CacheConstants.KEY_PRE_PASSPORT_OAUTHACCOUNT_BY_XLUSERID_TYPE + userId + ":" + type.name();
		return cache.del(key);
	}

	// 8.set or get PassportUserAccountEntity By userId

	@Override
	public UserDetailInfoEntity getUserInfoByUserId(Long userId) {
		String key = CacheConstants.KEY_PRE_PASSPORT_USERACCOUNT_BY_XLUSERID + userId;
		return cache.get(key, UserDetailInfoEntity.class);
	}

	@Override
	public Boolean setUserInfoByUserId(Long userId, UserDetailInfoEntity passportUserAccountEntity) {
		String key = CacheConstants.KEY_PRE_PASSPORT_USERACCOUNT_BY_XLUSERID + userId;
		return cache.set(key, passportUserAccountEntity, CacheConstants.EXPIRE_PASSPORT_USERACCOUNT_BY_XLUSERID) > 0;
	}

	@Override
	public Long delUserInfoByUserId(Long userId) {
		String key = CacheConstants.KEY_PRE_PASSPORT_USERACCOUNT_BY_XLUSERID + userId;
		return cache.del(key);
	}

}
