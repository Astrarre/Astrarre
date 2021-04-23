package io.github.astrarre.transfer.v0.api.participants;

import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.access.v0.api.provider.Provider;
import io.github.astrarre.transfer.v0.api.Insertable;
import io.github.astrarre.transfer.v0.api.Participant;
import io.github.astrarre.transfer.v0.api.Participants;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * a participant that directly delegates to another
 */
public interface DelegateParticipant<T> extends Participant<T>, Provider {
	Participant<T> getDelegate();

	@Override
	default @Nullable Object get(Access<?> access) {
		if(access == Participants.DIRECT_WRAPPERS) {
			return this.getDelegate();
		}
		return null;
	}

	@Override
	default void extract(@Nullable Transaction transaction, Insertable<T> insertable) {
		this.getDelegate().extract(transaction, insertable);
	}

	@Override
	default int extract(@Nullable Transaction transaction, @NotNull T type, int quantity) {
		return this.getDelegate().extract(transaction, type, quantity);
	}

	@Override
	default boolean isEmpty(@Nullable Transaction transaction) {
		return this.getDelegate().isEmpty(transaction);
	}

	@Override
	default void clear(@Nullable Transaction transaction) {
		this.getDelegate().clear(transaction);
	}

	@Override
	default int insert(@Nullable Transaction transaction, @NotNull T type, int quantity) {
		return this.getDelegate().insert(transaction, type, quantity);
	}

	@Override
	default boolean isFull(@Nullable Transaction transaction) {
		return this.getDelegate().isFull(transaction);
	}

	@Override
	default long getVersion() {
		return this.getDelegate().getVersion();
	}

	@Override
	default boolean supportsExtraction() {
		return this.getDelegate().supportsExtraction();
	}

	@Override
	default boolean supportsInsertion() {
		return this.getDelegate().supportsInsertion();
	}
}
