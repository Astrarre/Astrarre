package io.github.astrarre.transfer.v0.api;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import org.jetbrains.annotations.Nullable;

/**
 * For fields that will always be set together (you must call both key's set together) you can use the same transaction handler
 */
public class TransactionHandler {
	private final IntArrayList transactions = new IntArrayList();

	public void pop(Transaction transaction) {
		if (this.transactions.popInt() != transaction.getNestLevel()) {
			throw new IllegalStateException("Messed up Key state! The key was likely enlisted but never notified!");
		}
	}

	/**
	 * @return true if the transaction has never been seen before
	 */
	public boolean store(@Nullable Transaction transaction) {
		if (transaction == null) {
			if (this.transactions.isEmpty()) {
				return true;
			} else {
				throw new IllegalStateException("");
			}
		}

		int nest = transaction.getNestLevel();
		if (!this.transactions.isEmpty() && this.transactions.topInt() == nest) {
			return true;
		} else {
			this.transactions.push(nest);
			return false;
		}
	}
}
