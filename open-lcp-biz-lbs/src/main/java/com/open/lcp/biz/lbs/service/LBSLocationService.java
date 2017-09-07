package com.open.lcp.biz.lbs.service;

import com.open.lcp.biz.lbs.dto.LBSLocationDTO;
import com.open.lcp.biz.lbs.service.dao.entity.LBSLocationEntity;

public interface LBSLocationService {

	public int insertOrUpdateLocation(long locationCode, String locationName, double lng, double lat, String desc,
			long cityCode, String cityName);

	public int deleteLocation(long locationCode);

	public int updateLocationDesc(long locationCode, String desc);

	public void refreshLocationCache(long locationCode);

	public void refreshAllLocationCache();

	public LBSLocationEntity getLocation(long locationCode);

	public LBSLocationDTO locate(double lng, double lat);

}
