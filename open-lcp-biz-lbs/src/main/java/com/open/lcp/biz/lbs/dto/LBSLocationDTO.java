package com.open.lcp.biz.lbs.dto;

public class LBSLocationDTO {

	private long locationCode;

	private String locationName;

	private double lng;

	private double lat;

	private double distance;

	private String desc;

	private long cityCode;

	private String cityName;

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public long getLocationCode() {
		return locationCode;
	}

	public void setLocationCode(long locationCode) {
		this.locationCode = locationCode;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public long getCityCode() {
		return cityCode;
	}

	public void setCityCode(long cityCode) {
		this.cityCode = cityCode;
	}

	public String getCityName() {
		return cityName == null ? null : cityName.replaceAll("市", "");
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

}
