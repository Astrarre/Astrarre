package io.github.astrarre.transfer.v0.api;

import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import org.jetbrains.annotations.Nullable;

/**
 * a replacing participant is a participant that allows for items to be 'swapped out' for one another.
 * This is especially relavent for item based participants, who need explicit replace behavior to swap out items in the players hand. It is expected of
 * the replacing participant to intelligently prioritize replacing of relevant components of the inventory.
 * Eg. a player inventory should prioritize swapping based on the held item.
 * @param <T>
 */
public interface ReplacingParticipant<T> extends Participant<T> {
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
