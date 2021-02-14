package io.github.astrarre.transfer.v0.api.transaction.keys.generated;
// this class is autogenerated

import io.github.astrarre.transfer.v0.api.transaction.TransactionHandler;
import io.github.astrarre.transfer.v0.api.transaction.Key;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;

// @formatter:off
public class ByteKeyImpl extends Key.Byte {
	private final TransactionHandler handler = new TransactionHandler();
	private final ByteArrayList values = new ByteArrayList();

	public ByteKeyImpl(byte originalValue) {
		this.values.add(0, originalValue);
	}

	protected ByteKeyImpl() {
	}

	@Override
	public byte get(Transaction transaction) {
		if(transaction == null || values.isEmpty()) {
			return this.getRootValue();
		}
		return this.values.topByte();
	}

	@Override
	protected void onApply(Transaction transaction) {
		super.onApply(transaction);
		this.handler.pop(transaction);
		Transaction parent = transaction.getParent();
		this.set(parent, this.values.popByte());
	}

	@Override
	protected void onAbort(Transaction transaction) {
		this.values.popByte();
		this.handler.pop(transaction);
	}

	@Override
	public void set(Transaction transaction, byte val) {
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
	protected byte getRootValue() {
		return this.values.getByte(0);
	}

	/**
	 * @param val the 'true' value of the key
	 */
	protected void setRootValue(byte val) {
		this.values.set(0, val);
	}
}