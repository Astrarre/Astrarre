package io.github.astrarre.transfer.v0.api.transaction.keys;

import io.github.astrarre.transfer.v0.api.transaction.Key;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import io.github.astrarre.transfer.v0.api.transaction.TransactionHandler;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class ObjectKeyImpl<T> extends Key.Object<T> {
	protected final TransactionHandler handler = new TransactionHandler();
	protected final ObjectArrayList<T> values = new ObjectArrayList<>();

	public ObjectKeyImpl(T originalValue) {
		this.values.add(0, originalValue);
	}

	/**
	 * by default the value stack is used to hold the backing value of the key, but custom keys (like Inventory slots) have custom backing and thus do not
	 * need an extra entry in the stack
	 * @see #getRootValue()
	 * @see #setRootValue(java.lang.Object)
	 */
	protected ObjectKeyImpl() {
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

	@Override
	public T get(Transaction transaction) {
		if (transaction == null || this.values.isEmpty()) {
			return this.getRootValue();
		}

		return this.top(transaction);
	}

	protected T top(Transaction transaction) {
		return this.values.top();
	}

	/**
	 * This method gets the backing object of this key
	 */
	protected T getRootValue() {
		return this.values.get(0);
	}

	/**
	 * This method sets the backing object of this key
	 */
	protected void setRootValue(T val) {
		if(this.handler.inactive()) {
			if(this.values.isEmpty()) {
				this.values.add(0, val);
			} else {
				this.values.set(0, val);
			}
		} else {
			throw new IllegalStateException("Cannot set root value when in transaction!");
		}
	}
}
