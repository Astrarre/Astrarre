package io.github.astrarre.transfer.v0.api;

/**
 * Defined as 1/81,000 of a bucket, a droplet (dp) is the fluid unit standard of the astrarre transfer api
 */
public final class Droplet {
	/**
	 * one bucket in droplets
	 */
	public static final int BUCKET = 81_000;
	/**
	 * the number of droplets in a bucket
	 */
	public static final int BOTTLE = BUCKET / 3;

	/**
	 * @return the number of droplets in the fraction, throws an exception if not divisible
	 */
	public static int fraction(int numerator, int denominator) {
		long val = ((long) numerator) * BUCKET;
		long divided = val / denominator;
		if (val % denominator == 0 && divided <= Integer.MAX_VALUE && divided >= 0) {
			return (int) val;
		} else {
			throw new IllegalArgumentException("Invalid fraction: '" + numerator + "/" + denominator + "'");
		}
	}

	/**
	 * @return the number of droplets in the fraction, however, it will just approximate the fraction instead of throwing an exception
	 */
	public static int fuzzyFraction(int numerator, int denominator) {
		double val = numerator;
		val *= BUCKET;
		val /= denominator;
		if (val < 0) {
			val = 0;
		} else if (val > Integer.MAX_VALUE) {
			val = Integer.MAX_VALUE;
		}
		return (int) Math.round(val);
	}
}
