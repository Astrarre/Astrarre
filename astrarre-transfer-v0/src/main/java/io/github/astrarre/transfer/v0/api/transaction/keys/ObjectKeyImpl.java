package io.github.astrarre.transfer.v0.api.transaction.keys;

import io.github.astrarre.transfer.v0.api.transaction.Key;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import io.github.astrarre.transfer.v0.api.transaction.TransactionHandler;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class ObjectKeyImpl<T> extends Key.Object<T> {
	private final TransactionHandler handler;
	private final ObjectArrayList<T> values = new ObjectArrayList<>();

	public ObjectKeyImpl(T originalValue) {this(new TransactionHandler(), originalValue);}

	public ObjectKeyImpl(TransactionHandler handler, T originalValue) {
		this.handler = handler;
		this.values.add(0, originalValue);
	}

	/**
	 * by default the value stack is used to hold the backing value of the key, but custom keys (like Inventory slots) have custom backing and thus do not
	 * need an extra entry in the stack
	 * @see #getTrue()
	 * @see #setTrue(java.lang.Object)
	 */
	protected ObjectKeyImpl() {
		this(new TransactionHandler());
	}

	/**
	 * @see #ObjectKeyImpl()
	 */
	protected ObjectKeyImpl(TransactionHandler handler) {
		this.handler = handler;
	}

	@Override
	public void onApply(Transaction transaction) {
		this.handler.pop(transaction);
		Transaction parent = transaction.getParent();
		this.set(parent, this.values.pop());
	}

	@Override
	public void onAbort(Transaction transaction) {
		this.values.pop();
		this.handler.pop(transaction);
	}

	@Override
	public void set(Transaction transaction, T val) {
		if (transaction == null) {
			this.setTrue(val);
			return;
		}

		if (this.handler.store(transaction)) {
			this.values.set(this.values.size() - 1, val);
		} else {
			this.values.push(val);
			transaction.enlistKey(this);
		}
	}

	@Override
	public T get(Transaction transaction) {
		if (transaction == null) {
			return this.getTrue();
		}

		return this.values.top();
	}

	/**
	 * This method gets the backing object of this key
	 */
	protected T getTrue() {
		return this.values.get(0);
	}

	/**
	 * This method sets the backing object of this key
	 */
	protected void setTrue(T val) {
		this.values.set(0, val);
	}
}
