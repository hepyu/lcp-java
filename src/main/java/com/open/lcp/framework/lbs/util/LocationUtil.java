package com.open.lcp.framework.lbs.util;

import java.awt.geom.Point2D;
import java.util.List;

//http://blog.csdn.net/lijie18/article/details/54892715
public class LocationUtil {

	// public static void main(String[] args) {
	// // 被检测的经纬度点
	// PageData orderLocation = new PageData();
	// orderLocation.put("X", "117.228117");
	// orderLocation.put("Y", "31.830429");
	// // 商业区域（百度多边形区域经纬度集合）
	// String partitionLocation =
	// "31.839064_117.219116,31.83253_117.219403,31.828511_117.218146,31.826763_117.219259,31.826118_117.220517,31.822713_117.23586,31.822958_117.238375,31.838512_117.23798,31.839617_117.226194,31.839586_117.222925";
	// System.out.println(isInPolygon(orderLocation, partitionLocation));
	// }

	/**
	 * 判断当前位置是否在多边形区域内
	 * 
	 * @param orderLocation
	 *            当前点
	 * @param partitionLocation
	 *            区域顶点
	 * @return
	 */
	// public static boolean isInPolygon(PageData orderLocation, String
	// partitionLocation) {
	//
	// double p_x = Double.parseDouble(orderLocation.getString("X"));
	// double p_y = Double.parseDouble(orderLocation.getString("Y"));
	// Point2D.Double point = new Point2D.Double(p_x, p_y);
	//
	// List<Point2D.Double> pointList = new ArrayList<Point2D.Double>();
	// String[] strList = partitionLocation.split(",");
	//
	// for (String str : strList) {
	// String[] points = str.split("_");
	// double polygonPoint_x = Double.parseDouble(points[1]);
	// double polygonPoint_y = Double.parseDouble(points[0]);
	// Point2D.Double polygonPoint = new Point2D.Double(polygonPoint_x,
	// polygonPoint_y);
	// pointList.add(polygonPoint);
	// }
	// return IsPtInPoly(point, pointList);
	// }

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
}
