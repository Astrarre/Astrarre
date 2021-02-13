package io.github.astrarre.gui.v0.api.util;

public class ColorUtil {
	/**
	 * @return ARGB {@code 0xAARRGGBB} for example, red is {@code 0xFFFF0000}
	 */
	public static int getARGB(int alpha, int red, int green, int blue) {
		return (alpha << 24) | (red << 16) | (green << 8) | blue;
	}

	/**
	 * @return ARGB from RGB {@code 0xFFRRGGBB}
	 */
	public static int getARGB(int red, int green, int blue) {
		return getARGB(0xff, red, green, blue);
	}
}
