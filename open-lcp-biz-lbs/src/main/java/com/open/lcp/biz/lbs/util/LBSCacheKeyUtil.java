package com.open.lcp.biz.lbs.util;

public class LBSCacheKeyUtil {

	public static String getLocationCoordinateCacheKey(long locationCode) {
		return "location.info.v1." + locationCode;
	}

	public static String getCityInfoCacheKey(long cityCode) {
		return "city.info.v1." + cityCode;
	}
}
