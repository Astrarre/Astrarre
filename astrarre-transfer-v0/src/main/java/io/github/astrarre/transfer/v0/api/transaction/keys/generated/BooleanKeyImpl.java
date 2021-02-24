package io.github.astrarre.transfer.v0.api.transaction.keys.generated;
// this class is autogenerated

import io.github.astrarre.transfer.v0.api.transaction.Key;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import io.github.astrarre.transfer.v0.api.transaction.TransactionHandler;
import it.unimi.dsi.fastutil.booleans.BooleanArrayList;

// @formatter:off
public class BooleanKeyImpl extends Key.Boolean {
	private final TransactionHandler handler = new TransactionHandler();
	private final BooleanArrayList values = new BooleanArrayList();

	public BooleanKeyImpl(boolean originalValue) {
		this.values.add(0, originalValue);
	}

	protected BooleanKeyImpl() {
	}

	@Override
	public boolean get(Transaction transaction) {
		if(transaction == null || values.isEmpty()) {
			return this.getRootValue();
		}
		return this.values.topBoolean();
	}

	@Override
	protected void onApply(Transaction transaction) {
		super.onApply(transaction);
		this.handler.pop(transaction);
		Transaction parent = transaction.getParent();
		this.set(parent, this.values.popBoolean());
	}

	@Override
	protected void onAbort(Transaction transaction) {
		this.values.popBoolean();
		this.handler.pop(transaction);
	}

	@Override
	public void set(Transaction transaction, boolean val) {
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
	protected boolean getRootValue() {
		return this.values.getBoolean(0);
	}

	/**
	 * @param val the 'true' value of the key
	 */
	protected void setRootValue(boolean val) {
		this.values.set(0, val);
	}
}