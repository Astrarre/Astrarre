package io.github.astrarre.transfer.v0.api.keys;
import io.github.astrarre.transfer.v0.api.TransactionHandler;
import io.github.astrarre.transfer.v0.api.Key;
import io.github.astrarre.transfer.v0.api.Transaction;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class ObjectKeyImpl<T> extends Key.Object<T> {
	private final TransactionHandler handler;
	private final ObjectArrayList<T> values = new ObjectArrayList<>();

	public ObjectKeyImpl(TransactionHandler handler, T originalValue) {
		this.handler = handler;
		this.values.add(0, originalValue);
	}

	public ObjectKeyImpl(T originalValue) {this(new TransactionHandler(), originalValue);}

	@Override public T get(Transaction transaction) {return this.values.top();}

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
		if (this.handler.store(transaction)) {
			this.values.set(this.values.size() - 1, val);
		} else {
			this.values.push(val);
			transaction.enlistKey(this);
		}
	}
}
