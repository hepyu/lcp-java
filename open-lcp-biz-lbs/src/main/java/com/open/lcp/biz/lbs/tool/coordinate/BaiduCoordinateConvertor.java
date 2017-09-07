package com.open.lcp.biz.lbs.tool.coordinate;

import java.math.BigDecimal;

import com.open.lcp.biz.lbs.model.LngLat;

//http://wangbaiyuan.cn/baidu-map-api-position-offset-calibration-algorithm.html
//http://blog.csdn.net/w605283073/article/details/64906518
public class BaiduCoordinateConvertor {

	private static double x_pi = 3.14159265358979324 * 3000.0 / 180.0;

	/**
	 * 对double类型数据保留小数点后多少位 高德地图转码返回的就是 小数点后6位，为了统一封装一下
	 * 
	 * @param digit
	 *            位数
	 * @param in
	 *            输入
	 * @return 保留小数位后的数
	 */
	static double dataDigit(int digit, double in) {
		return new BigDecimal(in).setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue();

	}

	/**
	 * 将火星坐标转变成百度坐标
	 * 
	 * @param lngLat_gd
	 *            火星坐标（高德、腾讯地图坐标等）
	 * @return 百度坐标
	 */

	public static LngLat bd_encrypt(LngLat lngLat_gd) {
		double x = lngLat_gd.getLng(), y = lngLat_gd.getLat();
		double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * x_pi);
		double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * x_pi);
		return new LngLat(dataDigit(6, z * Math.cos(theta) + 0.0065), dataDigit(6, z * Math.sin(theta) + 0.006));

	}

	/**
	 * 将百度坐标转变成火星坐标
	 * 
	 * @param lngLat_bd
	 *            百度坐标（百度地图坐标）
	 * @return 火星坐标(高德、腾讯地图等)
	 */
	public static LngLat bd_decrypt(LngLat lngLat_bd) {
		double x = lngLat_bd.getLng() - 0.0065, y = lngLat_bd.getLat() - 0.006;
		double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi);
		double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi);
		return new LngLat(dataDigit(6, z * Math.cos(theta)), dataDigit(6, z * Math.sin(theta)));

	}

	// 测试代码
	public static void main(String[] args) {
		// LngLat lngLat_bd = new LngLat(120.153192, 30.25897);
		// System.out.println(bd_decrypt(lngLat_bd));

		LngLat lngLat_bd = new LngLat(116.346967302818, 39.9872830264316);
		System.out.println(bd_encrypt(lngLat_bd));
	}
}
