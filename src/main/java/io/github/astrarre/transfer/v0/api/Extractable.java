package io.github.astrarre.transfer.v0.api;

import io.github.astrarre.itemview.v0.fabric.TaggedItem;
import io.github.astrarre.transfer.internal.ExtractableParticipant;
import io.github.astrarre.transfer.internal.TransferInternal;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import io.github.astrarre.transfer.v0.api.util.Participants;
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
	 * @apiNote {@link TaggedItem} is not guaranteed to be the immutable kind
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
