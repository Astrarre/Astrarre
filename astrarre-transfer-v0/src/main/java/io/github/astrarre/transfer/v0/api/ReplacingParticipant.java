package io.github.astrarre.transfer.v0.api;

import io.github.astrarre.transfer.v0.api.participants.array.Slot;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * a replacing participant is a participant that allows for items to be 'swapped out' for one another.
 * This is especially relavent for item based participants, who need explicit replace behavior to swap out items in the players hand. It is expected of
 * the replacing participant to intelligently prioritize replacing of relevant components of the inventory.
 * Eg. a player inventory should prioritize swapping based on the held item.
 * @param <T>
 */
public interface ReplacingParticipant<T> extends Participant<T> {
	/**
	 * @return a new replacing participant that replaces items in the replacing slot by default (else revert to default behavior in {@link ReplacingParticipant#replace(Transaction, Object, int, Object, int)}
	 */
	static <T> ReplacingParticipant<T> of(Slot<T> replacingSlot, Participant<T> buffer) {
		return new ReplacingParticipant<T>() {
			@Override
			public boolean replace(@Nullable Transaction transaction, T target, int targetAmount, T replacement, int replacementAmount) {
				if(target.equals(replacingSlot.getKey(transaction))) {
					int quantity = replacingSlot.getQuantity(transaction);
					if(targetAmount == quantity) {
						return replacingSlot.set(transaction, replacement, replacementAmount);
					} else if(targetAmount < quantity) {
						try (Transaction action = Transaction.create(false)) {
							if (replacingSlot.extract(transaction, target, targetAmount) == targetAmount) {
								if(this.insert(transaction, replacement, replacementAmount) == replacementAmount) {
									action.commit();
									return true;
								}
							}
							return false;
						}
					}
				}
				return ReplacingParticipant.super.replace(transaction, target, targetAmount, replacement, replacementAmount);
			}

			@Override
			public void extract(@Nullable Transaction transaction, Insertable<T> insertable) {
				buffer.extract(transaction, insertable);
				if(insertable.isFull(transaction)) {
					return;
				}
				replacingSlot.extract(transaction, insertable);
			}

			@Override
			public int extract(@Nullable Transaction transaction, @NotNull T type, int quantity) {
				int count = buffer.extract(transaction, type, quantity);
				if(count != quantity) {
					count += replacingSlot.extract(transaction, type, quantity - count);
				}
				return count;
			}

			@Override
			public int insert(@Nullable Transaction transaction, @NotNull T type, int quantity) {
				int count = buffer.insert(transaction, type, quantity);
				if(count != quantity) {
					count += replacingSlot.insert(transaction, type, quantity - count);
				}
				return count;
			}
		};
	}

	default boolean replace(@Nullable Transaction transaction, T target, int targetAmount, T replacement, int replacementAmount) {
		try(Transaction transaction1 = Transaction.create()) {
			if(this.extract(transaction1, target, targetAmount) != targetAmount) {
				transaction1.abort();
				return false;
			}

			if(this.insert(transaction1, replacement, replacementAmount) != replacementAmount) {
				transaction1.abort();
				return false;
			}
		}

		return true;
	}
}
