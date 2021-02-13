package io.github.astrarre.transfer.v0.api.util;

import io.github.astrarre.transfer.v0.api.Insertable;
import io.github.astrarre.transfer.v0.api.Participant;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import org.jetbrains.annotations.Nullable;

public enum EmptyParticipant implements Participant<Object> {
	INSTANCE;

	// @formatter:off
	@Override public void extract(Transaction transaction, Insertable<Object> insertable) {}
	@Override public int extract(Transaction transaction, Object type, int amount) {return 0;}
	@Override public int insert(Transaction transaction, Object type, int amount) {return 0;}
	@Override public boolean isEmpty(@Nullable Transaction transaction) { return true; }
	@Override public boolean isFull(@Nullable Transaction transaction) { return true; }
	@Override public long getVersion(Transaction transaction) { return 0; }
	// @formatter:on

	@SuppressWarnings ("unchecked")
	public static <T> Participant<T> empty() {
		return (Participant<T>) INSTANCE;
	}
}
