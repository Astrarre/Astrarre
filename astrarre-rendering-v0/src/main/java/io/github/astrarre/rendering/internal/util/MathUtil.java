package io.github.astrarre.rendering.internal.util;

import io.github.astrarre.rendering.v0.api.util.Polygon;

public class MathUtil {
	public static void main(String[] args) {
		System.out.println(areCoplanar(0, 0, 0, 1, 1, 1, 1, 2, 0, 3, 3, 3));
	}

	public static boolean linesIntersect(float p1x, float p1y, float q1x, float q1y, float p2x, float p2y, float q2x, float q2y) {
		int o1 = rot(p1x, p1y, q1x, q1y, p2x, p2y);
		int o2 = rot(p1x, p1y, q1x, q1y, q2x, q2y);
		int o3 = rot(p2x, p2y, q2x, q2y, p1x, p1y);
		int o4 = rot(p2x, p2y, q2x, q2y, q1x, q1y);
		if (o1 != o2 && o3 != o4) {
			return true;
		}
		if (o1 == 0 && onSegment(p1x, p1y, p2x, p2y, q1x, q1y)) {
			return true;
		}
		if (o2 == 0 && onSegment(p1x, p1y, q2x, q2y, q1x, q1y)) {
			return true;
		}
		if (o3 == 0 && onSegment(p2x, p2y, p1x, p1y, q2x, q2y)) {
			return true;
		}
		return o4 == 0 && onSegment(p2x, p2y, q1x, q1y, q2x, q2y);
	}

	// 0 = coliner, 1 = clockwise, 2 = counterclockwise
	public static int rot(float px, float py, float qx, float qy, float rx, float ry) {
		float val = (qy - py) * (rx - qx) - (qx - px) * (ry - qy);

		if (Math.abs(val) < Polygon.EPSILON) {
			return 0;
		}
		return (val > 0) ? 1 : 2;
	}

	public static boolean onSegment(float px, float py, float qx, float qy, float rx, float ry) {
		return lessThanEqualTo(qx, Math.max(px, rx)) && greaterThanEqualTo(qx, Math.min(px, rx)) && lessThanEqualTo(qy, Math.max(py, ry)) && greaterThanEqualTo(qy, Math.min(py, ry));
	}

	private static boolean lessThanEqualTo(float a, float b) {
		return a < b || Math.abs(a - b) < Polygon.EPSILON;
	}

	private static boolean greaterThanEqualTo(float a, float b) {
		return a > b || Math.abs(a - b) < Polygon.EPSILON;
	}

	/**
	 * @return if the points are all coplanar
	 */
	public static boolean areCoplanar(float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x, float y, float z) {
		float a1 = x2 - x1;
		float b1 = y2 - y1;
		float c1 = z2 - z1;
		float a2 = x3 - x1;
		float b2 = y3 - y1;
		float c2 = z3 - z1;
		float a = b1 * c2 - b2 * c1;
		float b = a2 * c1 - a1 * c2;
		float c = a1 * b2 - b1 * a2;
		float d = (-a * x1 - b * y1 - c * z1);
		return Math.abs(a * x + b * y + c * z + d) < Polygon.EPSILON;
	}
}
