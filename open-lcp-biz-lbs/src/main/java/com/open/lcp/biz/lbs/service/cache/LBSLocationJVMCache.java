package com.open.lcp.biz.lbs.service.cache;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.open.lcp.biz.lbs.LBSConfiguration;
import com.open.lcp.biz.lbs.service.dao.LBSLocationDAO;
import com.open.lcp.biz.lbs.service.dao.entity.LBSLocationEntity;
import com.open.lcp.biz.lbs.util.LBSCacheKeyUtil;
import com.open.lcp.dbs.cache.redis.RedisX;

@Component
public class LBSLocationJVMCache {

	@Autowired
	private LBSLocationDAO locationCoordinateDao;

	@Resource(name = LBSConfiguration.BEAN_NAME_REDIS_LBS)
	private RedisX redisX;

	public LoadingCache<Long, List<Point2D.Double>> locationCodeToPoint2DListCache = CacheBuilder.newBuilder()
			.maximumSize(5000).refreshAfterWrite(60 * 10, TimeUnit.SECONDS)
			.build(new CacheLoader<Long, List<Point2D.Double>>() {

				@Override
				public List<Point2D.Double> load(Long locationCode) throws Exception {
					// String polygonstr =
					// redioGeoService.hget(KEY_LOCATION_POLYGON_MAP,
					// locationCode + "", String.class);
					// logger.info("locationCoordinateV2Cache.locationCode:"+locationCode+",polygonstr");

					LBSLocationEntity v2 = getLocationCoordinateFromRedisOrDb(locationCode);
					if (v2 == null) {
						return null;
					} else {
						List<Point2D.Double> list = new ArrayList<Point2D.Double>();
						// lat,lng用，号分隔，多个经纬度之间用；号分隔: lat1,lng1;lat2,lng2
						Point2D.Double d = null;
						String[] array = v2.getPolygon().split(";");
						String[] temp = null;
						for (String e : array) {
							try {
								temp = e.split(",");
								d = new Point2D.Double(Double.parseDouble(temp[1]), Double.parseDouble(temp[0]));
								list.add(d);
							} catch (Exception e1) {
							}
						}
						return list;
					}
				}

			});

	public LoadingCache<Long, LBSLocationEntity> locationCodeToDBEntityCache = CacheBuilder.newBuilder()
			.maximumSize(500).refreshAfterWrite(60 * 10, TimeUnit.SECONDS)
			.build(new CacheLoader<Long, LBSLocationEntity>() {

				@Override
				public LBSLocationEntity load(Long locationCode) throws Exception {
					return getLocationCoordinateFromRedisOrDb(locationCode);
				}

			});

	private LBSLocationEntity getLocationCoordinateFromRedisOrDb(long locationCode) {
		String cacheKey = LBSCacheKeyUtil.getLocationCoordinateCacheKey(locationCode);
		LBSLocationEntity coordinate = redisX.get(cacheKey, LBSLocationEntity.class);
		if (coordinate == null) {
			coordinate = locationCoordinateDao.getLocationCoordinate(locationCode);
			redisX.set(cacheKey, coordinate);
			redisX.expire(cacheKey, 10 * 60);
		}

		return coordinate;
	}

}
