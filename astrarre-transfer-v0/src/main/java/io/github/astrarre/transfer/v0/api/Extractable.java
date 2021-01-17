package io.github.astrarre.transfer.v0.api;

import io.github.astrarre.itemview.v0.api.item.ItemKey;
import io.github.astrarre.transfer.internal.TransferInternal;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import org.jetbrains.annotations.Nullable;

public interface Extractable<T> {
	/**
	 * attempt to insert the contents of the current inventory into the insertable
	 * @see Insertable#isFull(Transaction)
	 */
	void extract(Transaction transaction, Insertable<T> insertable);

	/**
	 * @param transaction the current transaction
	 * @apiNote {@link ItemKey} is not guaranteed to be the immutable kind
	 * @return the amount actually extracted
	 */
	int extract(Transaction transaction, T type, int amount);

	/**
	 * @return if true, extract should return 0 else undefined
	 */
	default boolean isEmpty(@Nullable Transaction transaction) {
		return false;
	}

	/**
	 * if the returned value is the same as the last time this was called, then it is safe to assume the participant has not changed
	 */
	default long getVersion(Transaction transaction) {
		return TransferInternal.version++;
	}
}
