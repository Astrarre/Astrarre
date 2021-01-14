package io.github.astrarre.transfer.v0.api;

import org.jetbrains.annotations.ApiStatus;

public abstract class Key {
	/**
	 * called when a transaction in which this key was enlisted was confirmed
	 *
	 * @param transaction the transaction being applied
	 * @see Transaction#getParent()
	 */
	@ApiStatus.OverrideOnly
	protected abstract void onApply(Transaction transaction);

	/**
	 * called when a transaction in which this key was enlisted was aborted
	 *
	 * @param transaction the transaction being aborted
	 */
	@ApiStatus.OverrideOnly
	protected abstract void onAbort(Transaction transaction);

	public static abstract class Int extends Key {
		/**
		 * technically you should only ever set data from the active transaction, the passed instance is for validation.
		 * @return true if the transaction has not been seen before (if a container 'set' twice)
		 */
		@ApiStatus.OverrideOnly
		protected abstract boolean store(Transaction transaction, int val);

		/**
		 * technically you should only ever retrieve data from the active transaction, the passed instance is for validation
		 */
		@ApiStatus.OverrideOnly
		protected abstract int get(Transaction transaction);

		/**
		 * @return the true value of the field, do not call this when in a transaction
		 */
		public abstract int get();
	}

	public static abstract class Float extends Key {
		/** @see Int#store(Transaction, int) */
		@ApiStatus.OverrideOnly
		protected abstract boolean store(Transaction transaction, float val);

		/** @see Int#store(Transaction, int) */
		@ApiStatus.OverrideOnly
		protected abstract float get(Transaction transaction);

		/** @see Int#get()*/
		public abstract float get();
	}

	public static abstract class Double extends Key {
		/** @see Int#store(Transaction, int) */
		@ApiStatus.OverrideOnly
		protected abstract boolean store(Transaction transaction, double val);

		/** @see Int#store(Transaction, int) */
		@ApiStatus.OverrideOnly
		protected abstract double get(Transaction transaction);

		/** @see Int#get()*/
		public abstract double get();
	}

	public static abstract class Long extends Key {
		/** @see Int#store(Transaction, int) */
		@ApiStatus.OverrideOnly
		protected abstract boolean store(Transaction transaction, long val);

		/** @see Int#store(Transaction, int) */
		@ApiStatus.OverrideOnly
		protected abstract long get(Transaction transaction);

		/** @see Int#get()*/
		public abstract long get();
	}

	public static abstract class Object<T> extends Key {
		/** @see Int#store(Transaction, int) */
		@ApiStatus.OverrideOnly
		protected abstract boolean store(Transaction transaction, T val);

		/** @see Int#store(Transaction, int) */
		@ApiStatus.OverrideOnly
		protected abstract T get(Transaction transaction);

		/** @see Int#get()*/
		public abstract T get();
	}
}
