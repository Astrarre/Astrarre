package io.github.astrarre.recipies.internal;

import java.util.Arrays;

import io.github.astrarre.util.v0.api.Validate;

/**
 * stores a table of integers, each filter stores one prime number that is multiplied in the table.
 * Which means if the number at that index is divisible by the prime number, it was reserved and is a valid char
 */
public class AsciiPrimeFilter {
	public static final AsciiPrimeFilter INSTANCE = new AsciiPrimeFilter();
	public static final int INTEGER = 2;
	static {
		INSTANCE.reserve('-', INTEGER);
		INSTANCE.reserve('0', INTEGER);
		INSTANCE.reserve('1', INTEGER);
		INSTANCE.reserve('2', INTEGER);
		INSTANCE.reserve('3', INTEGER);
		INSTANCE.reserve('4', INTEGER);
		INSTANCE.reserve('5', INTEGER);
		INSTANCE.reserve('6', INTEGER);
		INSTANCE.reserve('7', INTEGER);
		INSTANCE.reserve('8', INTEGER);
		INSTANCE.reserve('9', INTEGER);
	}

	private final int[] table = new int[128];
	public AsciiPrimeFilter() {
		Arrays.fill(this.table, 1);
	}

	/**
	 * @param c the character to reserve
	 * @param prime a prime number that represents this filter
	 */
	public void reserve(char c, int prime) {
		Validate.greaterThan(128, c, c + " is not an ascii char!");
		int val = this.table[c];
		this.table[c] = Math.multiplyExact(val, prime);
	}

	public boolean test(int c, int prime) {
		int[] table = this.table;
		return c >= 0 && c < table.length && (table[c] % prime) == 0;
	}
}
