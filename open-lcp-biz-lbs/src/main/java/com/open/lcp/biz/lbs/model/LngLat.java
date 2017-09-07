package com.open.lcp.biz.lbs.model;

public class LngLat {
	private double lng;// 经度
	private double lat;// 维度

	public LngLat() {
	}

	public LngLat(double lng, double lat) {
		this.lng = lng;
		this.lat = lat;
	}

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

	@Override
	public String toString() {
		return lng + "," + lat;
	}
}
