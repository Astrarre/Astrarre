package io.github.astrarre.transfer.internal;

import io.github.astrarre.transfer.v0.api.Key;
import io.github.astrarre.transfer.v0.api.Transaction;
import it.unimi.dsi.fastutil.ints.IntArrayList;

public class IntKeyImpl extends Key.Int {
	private final IntArrayList transactions = new IntArrayList(), values = new IntArrayList();

	@Override
	protected void onApply(Transaction transaction) {
		Transaction parent = transaction.getParent();
		this.transactions.popInt();
		parent.set(this, this.values.popInt());
	}

	@Override
	protected void onAbort(Transaction transaction) {
		// delete latest entry
		this.values.popInt();
		this.transactions.popInt();
	}

	@Override
	protected boolean store(Transaction transaction, int val) {
		int nest = transaction.getNestLevel();
		if (this.transactions.peekInt(0) == nest) {
			this.values.set(this.values.size() - 1, val);
			return false;
		} else {
			this.transactions.push(nest);
			this.values.push(val);
			return true;
		}
	}

	@Override
	protected int get(Transaction transaction) {
		return this.values.peekInt(0);
	}

	@Override
	public int get() {
		return this.values.getInt(0);
	}
}
