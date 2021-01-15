package io.github.astrarre.transfer.v0.api;

import org.jetbrains.annotations.Nullable;

public interface Extractable<T> {
	/**
	 * attempt to insert the contents of the current inventory into the insertable
	 * @see Insertable#isFull()
	 */
	void extract(Transaction transaction, Insertable<T> insertable);

	/**
	 * @param transaction the current transaction
	 * @return the amount actually extracted
	 */
	int extract(Transaction transaction, T type, int amount);

	/**
	 * @return if true, extract should return 0 else undefined
	 */
	default boolean isEmpty(@Nullable Transaction transaction) {
		return false;
	}
}
