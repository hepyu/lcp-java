package com.open.lcp.biz.lbs.service.dao.entity;

public class LBSCityEntity {

	private long cityCode;
	private String cityName;

	public long getCityCode() {
		return cityCode;
	}

	public void setCityCode(long cityCode) {
		this.cityCode = cityCode;
	}

	public String getCityName() {
		if (cityName == null) {
			return cityName;
		}
		return cityName.replaceAll("市", "");
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

}
