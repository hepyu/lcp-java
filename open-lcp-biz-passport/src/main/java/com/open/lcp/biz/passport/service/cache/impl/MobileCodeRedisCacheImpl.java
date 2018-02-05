package com.open.lcp.biz.passport.service.cache.impl;

import org.springframework.stereotype.Component;

import com.open.lcp.biz.passport.service.cache.CacheConstants;
import com.open.lcp.biz.passport.service.cache.MobileCodeCache;
import com.open.lcp.core.env.LcpResource;
import com.open.lcp.core.feature.user.api.MobileCodeType;
import com.open.lcp.dbs.cache.CacheX;
import com.open.lcp.dbs.cache.redis.RedisXFactory;

@Component
public class MobileCodeRedisCacheImpl implements MobileCodeCache {

	private final CacheX cache = RedisXFactory.loadRedisX(LcpResource.lcp_redis_core_feature_user);

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

}
