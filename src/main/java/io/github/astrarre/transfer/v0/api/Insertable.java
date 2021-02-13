package io.github.astrarre.transfer.v0.api;

import io.github.astrarre.transfer.internal.TransferInternal;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
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

	/**
	 * if the returned value is the same as the last time this was called, then it is safe to assume the participant has not changed
	 */
	default long getVersion(Transaction transaction) {
		return TransferInternal.version++;
	}
}
