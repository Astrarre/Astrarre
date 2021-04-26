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
	 * The number of droplets of a liquid resource needed to equal one block of the solid material for materials that require 9 'ingots' for one block.
	 * Eg. gold, diamonds, emeralds, iron, netherite, coal, slime
	 */
	public static final int BLOCK_9 = BUCKET;

	/**
	 * The number of droplets of a liquid resource needed to equal one 'ingot' or 'gem' of the solid material.
	 * Eg. iron ingot, gold ingot, diamond, emerald, netherite ingot, glowstone, redstone
	 */
	public static final int INGOT = BUCKET / 9;

	/**
	 * The number of droplets of a liquid resource needed to equal one block of the solid material for materials that require 4 'ingots' for one block.
	 * Eg. 1.17 copper, honey block, glowstone
	 */
	public static final int BLOCK_4 = INGOT * 4;

	/**
	 * The number of droplets of a liquid resource needed to equal one 'nugget' of the solid material.
	 * Eg. iron nugget, gold nugget
	 */
	public static final int NUGGET = INGOT / 9;

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

	/**
	 * multiplies the two numbers, if they would have overflown, return intmax
	 */
	public static int minMultiply(int val, int num) {
		int max = Integer.MAX_VALUE / num;
		if(val > max) {
			return Integer.MAX_VALUE;
		} else {
			return num * val;
		}
	}

	/**
	 * add the two numbers, if they would have overflown, return intmax
	 */
	public static int minSum(int val, int num) {
		int max = Integer.MAX_VALUE - num;
		if(val > max) {
			return Integer.MAX_VALUE;
		} else {
			return num + val;
		}
	}

	public static int fromMb(int mb) {
		return mb * 81;
	}
}
