package io.github.astrarre.gui.internal.util;

public class MathUtil {
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
		System.out.println(a + " " + b + " " + c);
		return Math.abs(a * x + b * y + c * z + d) < Float.MIN_NORMAL;
	}

	public static void main(String[] args) {
		areCoplanar(0, 0, 0, 1, 1, 1, 1, 2, 0, 3, 3, 3);
	}
}
