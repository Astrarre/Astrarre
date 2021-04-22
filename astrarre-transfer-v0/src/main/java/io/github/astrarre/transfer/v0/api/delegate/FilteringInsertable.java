package io.github.astrarre.transfer.v0.api.delegate;

import io.github.astrarre.transfer.v0.api.Insertable;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import org.jetbrains.annotations.Nullable;

public interface FilteringInsertable<T> extends Insertable<T> {
	boolean isValid(T object, int quantity);

	Insertable<T> delegate();

	@Override
	default int insert(@Nullable Transaction transaction, T type, int quantity) {
		if(this.isValid(type, quantity)) {
			return this.delegate().insert(transaction, type, quantity);
		}
		return 0;
	}

	@Override
	default boolean isFull(@Nullable Transaction transaction) {
		return this.delegate().isFull(transaction);
	}

	@Override
	default long getVersion() {
		return this.delegate().getVersion();
	}
}
