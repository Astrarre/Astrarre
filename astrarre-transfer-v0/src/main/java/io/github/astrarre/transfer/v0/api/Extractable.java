package io.github.astrarre.transfer.v0.api;

import io.github.astrarre.transfer.internal.participants.ExtractableParticipant;
import io.github.astrarre.transfer.internal.TransferInternal;
import io.github.astrarre.transfer.v0.api.filter.FilteringInsertable;
import io.github.astrarre.transfer.v0.api.participants.FixedObjectVolume;
import io.github.astrarre.transfer.v0.api.participants.ObjectVolume;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @see Droplet
 */
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
	int extract(@Nullable Transaction transaction, @NotNull T type, int quantity);

	interface Simple<T> extends Extractable<T> {
		@Override
		default int extract(@Nullable Transaction transaction, @NotNull T type, int quantity) {
			ObjectVolume<T> volume = new FixedObjectVolume<>(null, quantity);
			FilteringInsertable<T> filter = new FilteringInsertable<>((object, quantity1) -> object.equals(type), volume);
			this.extract(transaction, filter);
			return volume.getQuantity(transaction);
		}
	}

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
