package com.open.lcp.biz.lbs.util;

import java.awt.geom.Point2D;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import com.open.lcp.biz.lbs.model.LngLat;

public class LBSLocationUtil {

	/**
	 * 判断当前位置是否在多边形区域内
	 * 
	 * @param orderLocation
	 *            当前点
	 * @param partitionLocation
	 *            区域顶点
	 * @return
	 */
	public static boolean isInPolygon(double lng, double lat, List<Point2D.Double> pointList) {

		Point2D.Double point = new Point2D.Double(lat, lng);

		return IsPtInPoly(point, pointList);
	}

	/**
	 * 返回一个点是否在一个多边形区域内， 如果点位于多边形的顶点或边上，不算做点在多边形内，返回false
	 * 
	 * @param point
	 * @param polygon
	 * @return
	 */
	public static boolean checkWithJdkGeneralPath(Point2D.Double point, List<Point2D.Double> polygon) {
		java.awt.geom.GeneralPath p = new java.awt.geom.GeneralPath();
		Point2D.Double first = polygon.get(0);
		p.moveTo(first.x, first.y);
		polygon.remove(0);
		for (Point2D.Double d : polygon) {
			p.lineTo(d.x, d.y);
		}
		p.lineTo(first.x, first.y);
		p.closePath();
		return p.contains(point);
	}

	/**
	 * 判断点是否在多边形内，如果点位于多边形的顶点或边上，也算做点在多边形内，直接返回true
	 * 
	 * @param point
	 *            检测点
	 * @param pts
	 *            多边形的顶点
	 * @return 点在多边形内返回true,否则返回false
	 */
	public static boolean IsPtInPoly(Point2D.Double point, List<Point2D.Double> pts) {

		int N = pts.size();
		boolean boundOrVertex = true; // 如果点位于多边形的顶点或边上，也算做点在多边形内，直接返回true
		int intersectCount = 0;// cross points count of x
		double precision = 2e-10; // 浮点类型计算时候与0比较时候的容差
		Point2D.Double p1, p2;// neighbour bound vertices
		Point2D.Double p = point; // 当前点

		p1 = pts.get(0);// left vertex
		for (int i = 1; i <= N; ++i) {// check all rays
			if (p.equals(p1)) {
				return boundOrVertex;// p is an vertex
			}

			p2 = pts.get(i % N);// right vertex
			if (p.x < Math.min(p1.x, p2.x) || p.x > Math.max(p1.x, p2.x)) {// ray
																			// is
																			// outside
																			// of
																			// our
																			// interests
				p1 = p2;
				continue;// next ray left point
			}

			if (p.x > Math.min(p1.x, p2.x) && p.x < Math.max(p1.x, p2.x)) {// ray
																			// is
																			// crossing
																			// over
																			// by
																			// the
																			// algorithm
																			// (common
																			// part
																			// of)
				if (p.y <= Math.max(p1.y, p2.y)) {// x is before of ray
					if (p1.x == p2.x && p.y >= Math.min(p1.y, p2.y)) {// overlies
																		// on a
																		// horizontal
																		// ray
						return boundOrVertex;
					}

					if (p1.y == p2.y) {// ray is vertical
						if (p1.y == p.y) {// overlies on a vertical ray
							return boundOrVertex;
						} else {// before ray
							++intersectCount;
						}
					} else {// cross point on the left side
						double xinters = (p.x - p1.x) * (p2.y - p1.y) / (p2.x - p1.x) + p1.y;// cross
																								// point
																								// of
																								// y
						if (Math.abs(p.y - xinters) < precision) {// overlies on
																	// a ray
							return boundOrVertex;
						}

						if (p.y < xinters) {// before ray
							++intersectCount;
						}
					}
				}
			} else {// special case when ray is crossing through the vertex
				if (p.x == p2.x && p.y <= p2.y) {// p crossing over p2
					Point2D.Double p3 = pts.get((i + 1) % N); // next vertex
					if (p.x >= Math.min(p1.x, p3.x) && p.x <= Math.max(p1.x, p3.x)) {// p.x
																						// lies
																						// between
																						// p1.x
																						// &
																						// p3.x
						++intersectCount;
					} else {
						intersectCount += 2;
					}
				}
			}
			p1 = p2;// next ray left point
		}

		if (intersectCount % 2 == 0) {// 偶数在多边形外
			return false;
		} else { // 奇数在多边形内
			return true;
		}
	}

	static double dataDigit(int digit, double in) {
		return new BigDecimal(in).setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue();

	}

	private static double x_pi = 3.14159265358979324 * 3000.0 / 180.0;

	/**
	 * 将火星坐标转变成百度坐标
	 * 
	 * @param lngLat_gd
	 *            火星坐标（高德、腾讯地图坐标等）
	 * @return 百度坐标
	 */

	public static LngLat bd_encrypt(double lng, double lat) {
		double x = lng, y = lat;
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
	public static LngLat bd_decrypt(double lng, double lat) {
		double x = lng - 0.0065, y = lat - 0.006;
		double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi);
		double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi);
		return new LngLat(dataDigit(6, z * Math.cos(theta)), dataDigit(6, z * Math.sin(theta)));

	}

	public static LngLat getCenterPoint(List<LngLat> lngLatList) {
		int total = lngLatList.size();
		double X = 0, Y = 0, Z = 0;
		for (LngLat g : lngLatList) {
			double lat, lon, x, y, z;
			lat = g.getLat() * Math.PI / 180;
			lon = g.getLng() * Math.PI / 180;
			x = Math.cos(lat) * Math.cos(lon);
			y = Math.cos(lat) * Math.sin(lon);
			z = Math.sin(lat);
			X += x;
			Y += y;
			Z += z;
		}
		X = X / total;
		Y = Y / total;
		Z = Z / total;
		double Lon = Math.atan2(Y, X);
		double Hyp = Math.sqrt(X * X + Y * Y);
		double Lat = Math.atan2(Z, Hyp);
		return new LngLat(Lon * 180 / Math.PI, Lat * 180 / Math.PI);
	}

	public static void main(String[] args) throws Exception {

		String polygonstr = "116.34082,39.981771;116.341797,39.981764;116.341808,39.981313;116.33933,39.981238;116.339325,39.982251;116.340801,39.982284;116.34082,39.981771";

		List<Point2D.Double> list = new ArrayList<Point2D.Double>();
		// lat,lng用，号分隔，多个经纬度之间用；号分隔: lat1,lng1;lat2,lng2
		Point2D.Double d = null;
		String[] array = polygonstr.split(";");
		String[] temp = null;
		for (String e : array) {
			try {
				temp = e.split(",");
				d = new Point2D.Double(Double.parseDouble(temp[1]), Double.parseDouble(temp[0]));
				list.add(d);
			} catch (Exception e1) {
			}
		}
		// Boolean isIn = LocationUtil.isInPolygon(121.516665, 31.179941, list);
		// Boolean isIn = LocationUtil.isInPolygon(121.516665, 31.17994, list);
		// Boolean isIn = LocationUtil.isInPolygon(121.515594,31.17887, list);
		Boolean isIn = LBSLocationUtil.isInPolygon(116.346967302818, 39.9872830264316, list);

		System.out.println(isIn);
	}

	// public static void main(String[] args) {
	// // 被检测的经纬度点
	// // PageData orderLocation = new PageData();
	// // orderLocation.put("X", "117.228117");
	// // orderLocation.put("Y", "31.830429");
	// // 商业区域（百度多边形区域经纬度集合）
	// String[] partitionLocation =
	// "31.839064_117.219116,31.83253_117.219403,31.828511_117.218146,31.826763_117.219259,31.826118_117.220517,31.822713_117.23586,31.822958_117.238375,31.838512_117.23798,31.839617_117.226194,31.839586_117.222925"
	// .split(",");
	// List<Point2D.Double> pointList = new ArrayList<Point2D.Double>();
	// for (String str : partitionLocation) {
	// String[] points = str.split("_");
	// double polygonPoint_x = Double.parseDouble(points[1]);
	// double polygonPoint_y = Double.parseDouble(points[0]);
	// Point2D.Double polygonPoint = new Point2D.Double(polygonPoint_x,
	// polygonPoint_y);
	// pointList.add(polygonPoint);
	// }
	// System.out.println(isInPolygon(31.830429, 117.228117, pointList));
	// }
}
