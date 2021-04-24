package io.github.astrarre.transfer.v0.api.participants.array;

import io.github.astrarre.transfer.v0.api.Droplet;
import io.github.astrarre.transfer.v0.api.Insertable;
import io.github.astrarre.transfer.v0.api.Participant;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * a single slot in an {@link ArrayParticipant}
 */
public interface Slot<T> extends Participant<T> {
	/**
	 * @return the resource instance of the slot
	 */
	T getKey(@Nullable Transaction transaction);

	/**
	 * @return the quantity in that slot
	 */
	int getQuantity(@Nullable Transaction transaction);

	/**
	 * @return true if the value and quantity was succesfully set
	 */
	boolean set(@Nullable Transaction transaction, T key, int quantity);

	@Override
	default boolean isEmpty(@Nullable Transaction transaction) {
		return this.getQuantity(transaction) == 0;
	}

	/**
	 * extract an amount of the current key from the slot
	 * @return the amount extracted
	 */
	default int extract(@Nullable Transaction transaction, int quantity) {
		int toTake = Math.min(quantity, this.getQuantity(transaction));
		if(this.set(transaction, this.getKey(transaction), this.getQuantity(transaction) - toTake)) {
			return toTake;
		} else {
			return 0;
		}
	}

	/**
	 * @return the amount inserted
	 */
	@Override
	default int insert(@Nullable Transaction transaction, @NotNull T key, int quantity) {
		int result = Droplet.minSum(this.getQuantity(transaction), quantity);
		if(this.set(transaction, this.getKey(transaction), result)) {
			return result - this.getQuantity(transaction);
		}
		return 0;
	}

	@Override
	default void extract(@Nullable Transaction transaction, Insertable<T> insertable) {
		try(Transaction transaction1 = Transaction.create()) {
			int capacity = insertable.insert(transaction1, this.getKey(transaction), this.getQuantity(transaction));
			if(this.extract(transaction, capacity) != capacity) {
				transaction1.abort();
			}
		}
	}

	@Override
	default int extract(@Nullable Transaction transaction, @NotNull T type, int quantity) {
		if (type.equals(this.getKey(transaction))) {
			return this.extract(transaction, quantity);
		}
		return 0;
	}
}
