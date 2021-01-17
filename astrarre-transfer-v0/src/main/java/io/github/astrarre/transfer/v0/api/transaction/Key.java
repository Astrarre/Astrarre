package io.github.astrarre.transfer.v0.api.transaction;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public abstract class Key {
	/**
	 * called when a transaction in which this key was enlisted was confirmed
	 *
	 * @param transaction the transaction being applied
	 * @see Transaction#getParent()
	 */
	@ApiStatus.OverrideOnly
	protected abstract void onApply(@Nullable Transaction transaction);

	/**
	 * called when a transaction in which this key was enlisted was aborted
	 *
	 * @param transaction the transaction being aborted
	 */
	@ApiStatus.OverrideOnly
	protected abstract void onAbort(@Nullable Transaction transaction);

	public static abstract class Boolean extends Key {
		/** @see Int#set(Transaction, int) */
		public abstract void set(@Nullable Transaction transaction, boolean val);
		/** @see Int#get(Transaction) */
		public abstract boolean get(@Nullable Transaction transaction);
	}

	public static abstract class Byte extends Key {
		/** @see Int#set(Transaction, int) */
		public abstract void set(@Nullable Transaction transaction, byte val);
		/** @see Int#get(Transaction) */
		public abstract byte get(@Nullable Transaction transaction);
	}

	public static abstract class Char extends Key {
		/** @see Int#set(Transaction, int) */
		public abstract void set(@Nullable Transaction transaction, char val);
		/** @see Int#get(Transaction) */
		public abstract char get(@Nullable Transaction transaction);
	}

	public static abstract class Short extends Key {
		/** @see Int#set(Transaction, int) */
		public abstract void set(@Nullable Transaction transaction, short val);
		/** @see Int#get(Transaction) */
		public abstract short get(@Nullable Transaction transaction);
	}

	public static abstract class Int extends Key {
		/**
		 * set the value for the given transaction in the key.
		 * @implNote {@link @Nullable Transaction#enlistKey(Key)}
		 */
		public abstract void set(@Nullable Transaction transaction, int val);

		/**
		 * get the value for the given transaction in the key.
		 */
		public abstract int get(@Nullable Transaction transaction);
	}

	public static abstract class Float extends Key {
		/** @see Int#set(Transaction, int) */
		public abstract void set(@Nullable Transaction transaction, float val);
		/** @see Int#get(Transaction) */
		public abstract float get(@Nullable Transaction transaction);
	}

	public static abstract class Double extends Key {
		/** @see Int#set(Transaction, int) */
		public abstract void set(@Nullable Transaction transaction, double val);
		/** @see Int#get(Transaction) */
		public abstract double get(@Nullable Transaction transaction);
	}

	public static abstract class Long extends Key {
		/** @see Int#set(Transaction, int) */
		public abstract void set(@Nullable Transaction transaction, long val);
		/** @see Int#get(Transaction) */
		public abstract long get(@Nullable Transaction transaction);
	}

	public static abstract class Object<T> extends Key {
		/** @see Int#set(Transaction, int) */
		public abstract void set(@Nullable Transaction transaction, T val);
		/** @see Int#get(Transaction) */
		public abstract T get(@Nullable Transaction transaction);
	}
}
