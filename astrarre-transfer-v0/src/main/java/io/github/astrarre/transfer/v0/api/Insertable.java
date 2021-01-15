package io.github.astrarre.transfer.v0.api;

import org.jetbrains.annotations.Nullable;

public interface Insertable<T> {
	/**
	 * @param transaction the current transaction
	 * @return the amount actually inserted
	 */
	int insert(Transaction transaction, T type, int amount);

	/**
	 * @return if true, insert should return 0 else undefined
	 */
	default boolean isFull(@Nullable Transaction transaction) {
		return false;
	}
}
