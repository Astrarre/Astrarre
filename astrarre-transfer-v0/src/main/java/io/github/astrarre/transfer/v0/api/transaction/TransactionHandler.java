package io.github.astrarre.transfer.v0.api.transaction;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import org.jetbrains.annotations.NotNull;

/**
 * a little utility class to manage transactions in keys
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
	public boolean store(@NotNull Transaction transaction) {
		int nest = transaction.getNestLevel();
		if (!this.transactions.isEmpty() && this.transactions.topInt() == nest) {
			return true;
		} else {
			this.transactions.push(nest);
			return false;
		}
	}

	public boolean isTop(Transaction transaction) {
		int nest = transaction.getNestLevel();
		return !this.transactions.isEmpty() && this.transactions.topInt() == nest;
	}
}
