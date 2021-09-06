package io.github.astrarre.hash.impl;

import java.util.Arrays;

import it.unimi.dsi.fastutil.chars.CharOpenHashSet;
import it.unimi.dsi.fastutil.chars.CharSet;

import net.minecraft.util.math.MathHelper;

/**
 * a packed string is in the following format: [string len] [char id...].
 * Examples:
 *  "test": [4] [0, 1, 2, 0, 0, 0, 0, ...]
 *
 */
public final class IdentifierPacker {
	public static final CharSet VALID_CHARACTERS;
	/**
	 * maps from character to it's id for packing
	 */
	protected static final byte[] CHAR_TO_ID, ID_TO_CHAR;
	/**
	 * The number of bits each char takes up
	 */
	protected static final int BITS_PER_CHAR, CHAR_MASK;
	/**
	 * the maximum size of a packable string
	 */
	protected static final int MAX_SIZE;
	protected static final int STR_LEN_BITS_SIZE, STR_LEN_MASK;

	static {
		CharSet validChars = new CharOpenHashSet(50);
		byte[] toId = new byte[128], toChar = new byte[128];
		Arrays.fill(toId, (byte) -1);

		char max = 0, current = 0;
		max = load('_', current++, toId, toChar, validChars, max);
		max = load('-', current++, toId, toChar, validChars, max);
		for(char i = 'a'; i <= 'z'; i++) { // 26
			max = load(i, current++, toId, toChar, validChars, max);
		}
		for(char i = '0'; i <= '9'; i++) { // 10
			max = load(i, current++, toId, toChar, validChars, max);
		}

		max = load('/', current++, toId, toChar, validChars, max);
		max = load('.', current++, toId, toChar, validChars, max);
		max = load(':', current++, toId, toChar, validChars, max);

		CHAR_TO_ID = Arrays.copyOf(toId, max + 1);
		ID_TO_CHAR = Arrays.copyOf(toChar, current);
		VALID_CHARACTERS = validChars;

		BITS_PER_CHAR = MathHelper.log2DeBruijn(current);
		CHAR_MASK = (1 << BITS_PER_CHAR) - 1;
		STR_LEN_BITS_SIZE = Long.SIZE % BITS_PER_CHAR;
		STR_LEN_MASK = (1 << STR_LEN_BITS_SIZE) - 1;
		MAX_SIZE = Math.min(Long.SIZE / BITS_PER_CHAR, 1 << STR_LEN_BITS_SIZE) - 1;
	}

	static char load(char c, int id, byte[] toId, byte[] toChar, CharSet validChars, char max) {
		toId[c] = (byte) id;
		toChar[id] = (byte) c;
		validChars.add(c);
		return (char) Math.max(c, max);
	}

	// you can think of this as a stack of numbers

	/**
	 * this does not check if the string is a valid identifier string
	 * returns -1 if the string is too long
	 * @param str must be shorter than {@link #MAX_SIZE} and only contain chars in {@link #VALID_CHARACTERS}
	 */
	public static long pack(String str) {
		int len = str.length();
		if(len > MAX_SIZE) {
			return -1;
		}

		long packed = (len & STR_LEN_MASK); // first we push the length of the string onto the stack
		for(int i = 0; i < len; i++) {
			packed = packed << BITS_PER_CHAR | CHAR_TO_ID[str.charAt(i)]; // and then we push each char id onto the stack, which moves up all the previous ones
		}

		packed <<= (long) BITS_PER_CHAR * (MAX_SIZE - len);

		return packed;
	}

	public static String unpack(long packed) {
		int len = (int) (packed >>> (Long.SIZE - STR_LEN_BITS_SIZE));
		char[] buf = new char[len];
		for(int i = 0; i < len; i++) {
			long shifted = packed >>> BITS_PER_CHAR * (MAX_SIZE - (i + 1));
			buf[i] = (char) ID_TO_CHAR[(int) (CHAR_MASK & shifted)];
		}
		return new String(buf);
	}

	public static int getId(char c) {
		return CHAR_TO_ID[c];
	}

	public static char getId(int i) {
		return (char) ID_TO_CHAR[i];
	}
}
