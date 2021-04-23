package io.github.astrarre.transfer.v0.api.filter;

import io.github.astrarre.transfer.v0.api.Insertable;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A filtering insertable filters the resources that would be inserted into it's delegate
 * @param <T> {@link Insertable}
 */
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
		/**
		 * @param object the inserting type
		 * @param quantity the amount being inserted
		 * @return true if the inserting query should be passed on to the delegate
		 */
		boolean isValid(T object, int quantity);
	}
}
