package io.github.astrarre.transfer.v0.api.filter;

import io.github.astrarre.transfer.v0.api.Insertable;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FilteringInsertable<T> implements Insertable<T> {
	public final Filter<T> filter;
	public final Insertable<T> delegate;

	public FilteringInsertable(Filter<T> valid, Insertable<T> delegate) {
		this.filter = valid;
		this.delegate = delegate;
	}

	@Override
	public int insert(@Nullable Transaction transaction, @NotNull T type, int quantity) {
		if (this.filter.isValid(type, quantity)) {
			return this.delegate.insert(transaction, type, quantity);
		}
		return 0;
	}

	@Override
	public boolean isFull(@Nullable Transaction transaction) {
		return this.delegate.isFull(transaction);
	}

	@Override
	public long getVersion() {
		return this.delegate.getVersion();
	}

	public interface Filter<T> {
		boolean isValid(T object, int quantity);
	}
}
