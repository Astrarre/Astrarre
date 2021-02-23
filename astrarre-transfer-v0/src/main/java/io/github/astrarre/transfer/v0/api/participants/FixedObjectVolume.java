package io.github.astrarre.transfer.v0.api.participants;

import io.github.astrarre.transfer.v0.api.transaction.Transaction;

public class FixedObjectVolume<T> extends ObjectVolume<T> {
	protected int max;

	public FixedObjectVolume(T empty, int max) {
		super(empty);
		this.max = max;
	}

	public FixedObjectVolume(T empty, T object, int quantity, int max) {
		super(empty, object, quantity);
		this.max = max;
	}


	@Override
	public int insert(Transaction transaction, T type, int quantity) {
		int currentCount = this.quantity.get(transaction);
		return super.insert(transaction, type, Math.min(this.getMax(transaction) - currentCount, Math.min(this.getMaxStackSize(type) - currentCount, quantity)));
	}

	/**
	 * @return if the maximum size of the container is dynamic, this can be overriden
	 */
	public int getMax(Transaction transaction) {
		return this.max;
	}

	public int getMaxStackSize(T type) {
		return Integer.MAX_VALUE;
	}
}
