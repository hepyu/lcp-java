package com.open.lcp.biz.lbs.service.impl;

import java.awt.geom.Point2D;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.open.lcp.biz.lbs.LBSConfiguration;
import com.open.lcp.biz.lbs.dto.LBSLocationDTO;
import com.open.lcp.biz.lbs.model.LngLat;
import com.open.lcp.biz.lbs.service.LBSLocationService;
import com.open.lcp.biz.lbs.service.cache.LBSLocationJVMCache;
import com.open.lcp.biz.lbs.service.dao.LBSDeviceDAO;
import com.open.lcp.biz.lbs.service.dao.LBSLocationDAO;
import com.open.lcp.biz.lbs.service.dao.entity.LBSLocationEntity;
import com.open.lcp.biz.lbs.util.LBSCacheKeyUtil;
import com.open.lcp.biz.lbs.util.LBSLocationUtil;
import com.open.lcp.dbs.cache.CacheX;

import redis.clients.jedis.GeoRadiusResponse;
import redis.clients.jedis.GeoUnit;
import redis.clients.jedis.params.geo.GeoRadiusParam;

@Component
public class LBSLocationServiceImpl implements LBSLocationService {

	private static final Logger logger = LoggerFactory.getLogger(LBSLocationServiceImpl.class);

	@Resource(name = LBSConfiguration.BEAN_NAME_REDIS_LBS)
	private CacheX cacheX;

	@Autowired
	private LBSLocationJVMCache locationJVMCache;

	@Autowired
	private LBSLocationDAO locationCoordinateDao;

	@Autowired
	private LBSDeviceDAO deviceCoordinateDao;

	private final String KEY_LOCATION_GEO_MAP = "location.geo.map.v1";

	// 单位：M
	private final long GEO_RADIUS = 20000;

	@Override
	public int insertOrUpdateLocation(long locationCode, String locationName, double lng, double lat, String desc,
			long cityCode, String cityName) {
		long now = System.currentTimeMillis();

		LBSLocationEntity coordinate = new LBSLocationEntity();
		coordinate.setCtime(now);
		coordinate.setLat(lat);
		coordinate.setLng(lng);
		coordinate.setLocationCode(locationCode);
		coordinate.setLocationName(locationName);
		coordinate.setDescription(desc);
		coordinate.setUtime(now);
		coordinate.setCityCode(cityCode);
		coordinate.setCityName(cityName);

		int result = locationCoordinateDao.insertOrUpdateLocationCoordinate(coordinate);

		if (result > 0) {
			cacheX.geoAdd(KEY_LOCATION_GEO_MAP, coordinate.getLng(), coordinate.getLat(), coordinate.getLocationCode());

			String cacheKey = LBSCacheKeyUtil.getLocationCoordinateCacheKey(locationCode);
			cacheX.set(cacheKey, coordinate);
			locationJVMCache.locationCodeToDBEntityCache.refresh(locationCode);
		}
		return result;
	}

	@Override
	public int deleteLocation(long locationCode) {
		int result = locationCoordinateDao.deleteLocationCoordinate(locationCode);
		if (result > 0) {
			this.refreshLocationCache(locationCode);
		}
		return result;
	}

	@Override
	public int updateLocationDesc(long locationCode, String description) {

		String cacheKey = null;

		int result = locationCoordinateDao.updateLocationDesc(locationCode, description);

		if (result > 0) {
			cacheKey = LBSCacheKeyUtil.getLocationCoordinateCacheKey(locationCode);
			cacheX.del(cacheKey);
			locationJVMCache.locationCodeToDBEntityCache.refresh(locationCode);
		}

		return result;
	}

	@Override
	public LBSLocationEntity getLocation(long locationCode) {
		try {
			return locationJVMCache.locationCodeToDBEntityCache.get(locationCode);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public void refreshLocationCache(long locationCode) {
		String cacheKey = LBSCacheKeyUtil.getLocationCoordinateCacheKey(locationCode);
		cacheX.del(cacheKey);
		cacheX.zrem(KEY_LOCATION_GEO_MAP, locationCode);
		locationJVMCache.locationCodeToDBEntityCache.refresh(locationCode);
	}

	// TODO 需要优化
	@Override
	public void refreshAllLocationCache() {
		cacheX.zremrangebyrank(KEY_LOCATION_GEO_MAP, 0, 100000);
		new Thread(new Runnable() {

			@Override
			public void run() {
				long startLocationCode = 0;
				List<LBSLocationEntity> list = null;

				while (true) {
					list = locationCoordinateDao.listLocationCoordinate(startLocationCode, 100);

					if (list == null || list.isEmpty()) {
						break;
					}

					for (LBSLocationEntity coordinate : list) {
						logger.info("reload-location:" + coordinate.getCityCode() + coordinate.getCityName());
						String polygonstr = coordinate.getPolygon();
						String[] polygonArray = polygonstr.split(";");

						for (String ele : polygonArray) {
							try {
								String[] temp = ele.split(",");

								logger.warn(String.format("reload-locaton:locationCode:%s,%s",
										coordinate.getLocationCode(), coordinate.getLocationCode()));
								// clearLocationCache(coordinate.getLocationCode());
								cacheX.geoAdd(KEY_LOCATION_GEO_MAP, Double.parseDouble(temp[0]),
										Double.parseDouble(temp[1]), coordinate.getLocationCode());
								// redioGeoService.hset(KEY_LOCATION_POLYGON_MAP,
								// coordinate.getLocationCode() + "",
								// coordinate.getPolygon());
							} catch (Exception e) {
								logger.warn(e.getMessage(), e);
							}
						}
					}

					startLocationCode = list.get(list.size() - 1).getLocationCode();
				}
			}
		}).start();
	}

	@Override
	public LBSLocationDTO locate(double lng, double lat) {
		LngLat lngLat = LBSLocationUtil.bd_encrypt(lng, lat);
		lng = lngLat.getLng();
		lat = lngLat.getLat();
		int fetchCount = 3;
		List<GeoRadiusResponse> list = geoNearest(KEY_LOCATION_GEO_MAP, lng, lat, GEO_RADIUS, fetchCount, true, true,
				"asc", GeoUnit.M);
		if (list == null || list.isEmpty()) {
			return null;
		} else {
			LBSLocationDTO dto = new LBSLocationDTO();
			List<Point2D.Double> pointList = null;
			boolean hitSpecial = false;
			for (int i = 0; i < list.size(); i++) {

				GeoRadiusResponse resp = list.get(i);
				long locationCode = Long.parseLong(resp.getMemberByString());

				try {
					pointList = locationJVMCache.locationCodeToPoint2DListCache.get(locationCode);

					logger.warn("locate-locationCode:" + locationCode + ", hitSpecial:" + hitSpecial);

					boolean isIn = LBSLocationUtil.isInPolygon(lng, lat, pointList);
					if (isIn) {
						dto = new LBSLocationDTO();

						dto.setLat(resp.getCoordinate().getLatitude());
						dto.setLng(resp.getCoordinate().getLongitude());
						// dto.setLocationCode(locationCode);
						dto.setDistance(resp.getDistance());

						LBSLocationEntity temp = null;
						try {
							temp = locationJVMCache.locationCodeToDBEntityCache.get(locationCode);
						} catch (Exception e) {
						}

						if (temp != null) {
							dto.setLocationName(temp.getLocationName());
							dto.setDesc(temp.getDescription());
							dto.setLocationCode(temp.getLocationCode());
							dto.setCityCode(temp.getCityCode());
							dto.setCityName(temp.getCityName());
						}

						return dto;
					}
				} catch (Exception e) {
					logger.warn(e.getMessage(), e);
				}
			}
		}
		return null;
	}

	private List<GeoRadiusResponse> geoNearest(String key, double longitude, double latitude, double radius, int count,
			boolean isWithCoord, boolean isWithDist, String distOrder, GeoUnit geoUnit) {
		GeoRadiusParam param = GeoRadiusParam.geoRadiusParam().count(count);
		if ("asc".equalsIgnoreCase(distOrder)) {
			param.sortAscending();
		} else {
			param.sortDescending();
		}
		if (isWithCoord) {
			param.withCoord();
		}
		if (isWithDist) {
			param.withDist();
		}
		if (geoUnit == null) {
			geoUnit = GeoUnit.KM;
		}
		return cacheX.geoRadius(key, longitude, latitude, radius, geoUnit, param);
	}

	// private static Gson gson = new Gson();

	// public static void main(String[] args) {
	// CloseableHttpClient httpClient = new HttpClientConfig().httpClient();
	//
	// double lat = 31.163469601475764000;
	// double lng = 121.359710300924680000;
	//
	// long cityCode = 0;
	// String cityName = "";
	//
	// //
	// "http://api.map.baidu.com/geocoder/v2/?callback=renderReverse&location=31.163469601475764000,121.359710300924680000&output=json&pois=1&ak=mbvrO7IGLzycgOohGDR1hMknEUG8GlsY");
	//
	// String result = HttpUtil.get(httpClient,
	// "http://api.map.baidu.com/geocoder/v2/?callback=renderReverse&location="
	// + lat + "," + lng
	// + "&output=json&pois=1&ak=IBwqS5qYdUt6mWGzjPoi4TGDqkgEI4f0");
	// System.out.println("result:" + result);
	// if (!StringUtils.isEmpty(result)) {
	// result = result.replace("renderReverse&&renderReverse(", "");
	// int len = result.length();
	// result = result.substring(0, len - 1);
	//
	// JSONObject jobj = JSONObject.parseObject(result);
	// if (jobj != null && jobj.containsKey("status")) {
	// if (jobj.getIntValue("status") == 0) {
	// if (jobj.containsKey("result")) {
	// JSONObject jdata = jobj.getJSONObject("result");
	// if (jdata.containsKey("cityCode")) {
	// cityCode = jdata.getIntValue("cityCode");
	// // System.out.println(jdata.getIntValue("cityCode"));
	// }
	// if (jdata.containsKey("addressComponent")) {
	// JSONObject temp = jdata.getJSONObject("addressComponent");
	// if (temp.containsKey("city")) {
	// cityName = temp.getString("city");
	// }
	// }
	// }
	// }
	// }
	// }
	//
	// System.out.println(cityCode);
	// System.out.println(cityName);
	// }

}
