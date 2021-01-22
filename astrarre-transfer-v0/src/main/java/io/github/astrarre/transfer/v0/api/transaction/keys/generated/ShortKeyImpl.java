package io.github.astrarre.transfer.v0.api.transaction.keys.generated;
// this class is autogenerated

import io.github.astrarre.transfer.v0.api.transaction.TransactionHandler;
import io.github.astrarre.transfer.v0.api.transaction.Key;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;

// @formatter:off
public class ShortKeyImpl extends Key.Short {
	private final TransactionHandler handler = new TransactionHandler();
	private final ShortArrayList values = new ShortArrayList();

	public ShortKeyImpl(short originalValue) {
		this.values.add(0, originalValue);
	}

	protected ShortKeyImpl() {
	}

	@Override
	public short get(Transaction transaction) {
		if(transaction == null || values.isEmpty()) {
			return this.getRootValue();
		}
		return this.values.topShort();
	}

	@Override
	protected void onApply(Transaction transaction) {
		this.handler.pop(transaction);
		Transaction parent = transaction.getParent();
		this.set(parent, this.values.popShort());
	}

	@Override
	protected void onAbort(Transaction transaction) {
		this.values.popShort();
		this.handler.pop(transaction);
	}

	@Override
	public void set(Transaction transaction, short val) {
		if(transaction == null) {
			this.setRootValue(val);
			return;
		}

		if (this.handler.store(transaction)) {
			this.values.set(this.values.size() - 1, val);
		} else {
			this.values.push(val);
			transaction.enlistKey(this);
		}
	}

	/**
    * @return the 'true' value of the key
    */
	protected short getRootValue() {
		return this.values.getShort(0);
	}

	/**
	 * @param val the 'true' value of the key
	 */
	protected void setRootValue(short val) {
		this.values.set(0, val);
	}
}
