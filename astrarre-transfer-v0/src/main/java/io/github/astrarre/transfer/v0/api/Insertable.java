package io.github.astrarre.transfer.v0.api;

import io.github.astrarre.transfer.internal.participants.InsertableParticipant;
import io.github.astrarre.transfer.internal.TransferInternal;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @see Droplet
 */
public interface Insertable<T> {
	static <T> Participant<T> asParticipant(Insertable<T> insertable) {
		return new InsertableParticipant<>(insertable);
	}

	/**
	 * @param transaction the current transaction
	 * @return the quantity actually inserted
	 */
	int insert(@Nullable Transaction transaction, @NotNull T type, int quantity);

	/**
	 * This should only be implemented if checking if the container is full is faster than just inserting.
	 * Eg. checking if an inventory is full is about as fast as actually inserting an item in (same complexity), so it would not have an isFull function
	 * @return if true, insert should return 0 else undefined
	 */
	default boolean isFull(@Nullable Transaction transaction) {
		return false;
	}

	/**
	 * if the returned value is the same as the last time this was called, then it is safe to assume the participant has not changed.
	 * This is invalid within a transaction
	 */
	default long getVersion() {
		return TransferInternal.version++;
	}
}
