package io.github.astrarre.transfer.v0.api;

import io.github.astrarre.transfer.v0.api.participants.ExtractableParticipant;
import io.github.astrarre.transfer.internal.fabric.TransferInternal;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import org.jetbrains.annotations.Nullable;

public interface Extractable<T> {
	static <T> Participant<T> asParticipant(Extractable<T> extractable) {
		return new ExtractableParticipant<>(extractable);
	}

	/**
	 * attempt to insert the contents of the current inventory into the insertable.
	 * @see Insertable#isFull(Transaction)
	 */
	void extract(@Nullable Transaction transaction, Insertable<T> insertable);

	/**
	 * @param transaction the current transaction
	 * @return the quantity actually extracted
	 */
	int extract(@Nullable Transaction transaction, T type, int quantity);

	/**
	 * @return if true, extract should return 0 else undefined
	 */
	default boolean isEmpty(@Nullable Transaction transaction) {
		return false;
	}

	/**
	 * <b>TRIES</b> to clear the extractable, this isn't guaranteed, some inventories may have immutable parts
	 */
	default void clear(@Nullable Transaction transaction) {
		this.extract(transaction, Participants.VOIDING.cast());
	}

	/**
	 * if the returned value is the same as the last time this was called, then it is safe to assume the participant has not changed
	 *
	 * this is not valid within a transaction
	 *
	 * it is ok to return a hash instead of a strict version because the consequences are usually small, and the chances are low
	 */
	default long getVersion() {
		return TransferInternal.version++;
	}
}
