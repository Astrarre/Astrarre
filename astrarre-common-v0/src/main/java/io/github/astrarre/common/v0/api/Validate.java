package io.github.astrarre.common.v0.api;

public class Validate {
	public static void void_(Object object) {}

	/**
	 * @throws IllegalArgumentException if {@code val} < 0
	 * @param name the name of the parameter
	 * @return {@code val}
	 */
	public static int positive(int val, String name) {
		return greaterThanEqualTo(val, 0, name);
	}

	/**
	 * @throws IllegalArgumentException if {@code val} <= {@code comp}
	 * @param name the name of the parameter
	 * @return {@code val}
	 */
	public static int greaterThan(int val, int comp, String name) {
		if(val > comp) {
			return val;
		}
		throw new IllegalArgumentException(String.format("%s (%d) <= %d!", name, val, comp));
	}

	/**
	 * @throws IllegalArgumentException if {@code val} < {@code comp}
	 * @param name the name of the parameter
	 * @return {@code val}
	 */
	public static int greaterThanEqualTo(int val, int comp, String name) {
		if(val >= comp) {
			return val;
		}
		throw new IllegalArgumentException(String.format("%s (%d) < %d!", name, val, comp));
	}
}
