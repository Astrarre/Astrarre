package io.github.astrarre.util.v0.api;

public class Parsing {
	private static final String TRUE = "true";
	private static final String FALSE = "false";
	private static final String NULL = "null";
	private static final String NAN = "NaN";
	private static final String INF = "inf";
	private static final String INFINITY = "infinity";
	private static final String NEGATIVE_INF = "-inf";
	private static final String NEGATIVE_INFINITY = "-infinity";

	public static boolean isNull(String key, CharSequence buffer) {
		return NULL.contentEquals(buffer);
	}

	public static boolean getBoolean(String key, CharSequence sequence) {
		if(TRUE.contentEquals(sequence)) {
			return true;
		} else if(FALSE.contentEquals(sequence)) {
			return false;
		}
		throw new IllegalArgumentException(key + " is invalid boolean!");
	}

	public static double getDouble(String key, CharSequence sequence) {
		if(NAN.contentEquals(sequence)) {
			return Double.NaN;
		} else if(INF.contentEquals(sequence) || INFINITY.contentEquals(sequence)) {
			return Double.POSITIVE_INFINITY;
		} else if(NEGATIVE_INF.contentEquals(sequence) || NEGATIVE_INFINITY.contentEquals(sequence)) {
			return Double.NEGATIVE_INFINITY;
		}

		double val = 0;
		long exp = 1;
		int end = sequence.length()-1;
		for (int i = end; i >= 0; i--) {
			char c = sequence.charAt(i);

			if (i == 0 && c == '-') {
				val *= -1;
				continue;
			}
			if (c == ',' || c == '_') { // , > .
				continue;
			}
			if (c == '.') {
				int log10 = (int)Math.log10(val) + 1;
				val = Math.pow(.1, log10) * val;
				exp = 1;
				continue;
			}
			if (c < '0' || c > '9') {
				throw new IllegalArgumentException(key + " is an invalid decimal!");
			}
			val = val + (c - 48) * exp;
			exp *= 10;
		}

		return val;
	}

	public static int getInt(String key, CharSequence sequence) {
		long l = getLong(key, sequence);
		if (l > Integer.MAX_VALUE || l < Integer.MIN_VALUE) {
			throw new IllegalArgumentException(key + " is too large!");
		}
		return (int) l;
	}

	static long getLong(String key, CharSequence sequence) {
		long val = 0;
		long exp = 1;
		for (int i = sequence.length() - 1; i >= 0; i--) {
			char c = sequence.charAt(i);
			if (i == 0 && c == '-') {
				val *= -1;
				continue;
			}
			if (c == ',' || c == '_') { // , > .
				continue;
			}
			if (c < 48 || c > 57) {
				throw new IllegalArgumentException(key + " is an invalid integer!");
			}
			long toAdd = (c - 48);
			if ((Long.MAX_VALUE - val) / exp < toAdd) {
				throw new IllegalArgumentException(key + " is too large!");
			}
			val = val + toAdd * exp;
			exp *= 10;
		}

		return val;
	}

	public static String getString(String key, CharSequence sequence) {
		return sequence.toString();
	}
}
