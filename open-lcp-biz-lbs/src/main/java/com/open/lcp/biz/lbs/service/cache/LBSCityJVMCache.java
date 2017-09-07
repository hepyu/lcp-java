package com.open.lcp.biz.lbs.service.cache;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.open.lcp.biz.lbs.LBSConfiguration;
import com.open.lcp.biz.lbs.service.dao.LBSDeviceDAO;
import com.open.lcp.biz.lbs.service.dao.entity.LBSCityEntity;
import com.open.lcp.biz.lbs.service.dao.entity.LBSDeviceEntity;
import com.open.lcp.biz.lbs.util.LBSCacheKeyUtil;
import com.open.lcp.dbs.cache.redis.RedisX;

@Component
public class LBSCityJVMCache {

	@Resource(name = LBSConfiguration.BEAN_NAME_REDIS_LBS)
	private RedisX redisX;

	@Autowired
	private LBSDeviceDAO deviceCoordinateDao;

	public LoadingCache<Long, LBSCityEntity> cityCodeToDBEntityCache = CacheBuilder.newBuilder().maximumSize(1000)
			.refreshAfterWrite(60 * 10, TimeUnit.SECONDS).build(new CacheLoader<Long, LBSCityEntity>() {

				@Override
				public LBSCityEntity load(Long cityCode) throws Exception {

					LBSCityEntity city = getCityInfoFromRedisOrDb(cityCode);
					return city;
				}

			});

	private LBSCityEntity getCityInfoFromRedisOrDb(long cityCode) {
		String cacheKey = LBSCacheKeyUtil.getCityInfoCacheKey(cityCode);
		LBSCityEntity city = redisX.get(cacheKey, LBSCityEntity.class);
		if (city == null) {
			LBSDeviceEntity dc = deviceCoordinateDao.getCityInfo(cityCode);
			if (dc == null) {
				return null;
			} else {
				city = new LBSCityEntity();
				city.setCityCode(dc.getCityCode());
				city.setCityName(dc.getCityName());
				redisX.set(cacheKey, city);
				redisX.expire(cacheKey, 5 * 60);
				return city;
			}
		} else {
			return city;
		}
	}
}
