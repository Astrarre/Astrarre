package io.github.astrarre.transfer.internal.participants;

import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.access.v0.api.provider.Provider;
import io.github.astrarre.transfer.v0.api.Extractable;
import io.github.astrarre.transfer.v0.api.Insertable;
import io.github.astrarre.transfer.v0.api.Participant;
import io.github.astrarre.transfer.v0.api.Participants;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * a participant wrapper over Extractable
 * @param <T>
 */
public final class ExtractableParticipant<T> implements Participant<T>, Provider {
	public final Extractable<T> extractable;

	public ExtractableParticipant(Extractable<T> extractable) {
		this.extractable = extractable;
	}

	@Override
	public void extract(@Nullable Transaction transaction, Insertable<T> insertable) {
		this.extractable.extract(transaction, insertable);
	}

	@Override
	public int extract(@Nullable Transaction transaction, @NotNull T type, int quantity) {
		return this.extractable.extract(transaction, type, quantity);
	}

	@Override
	public int insert(@Nullable Transaction transaction, @NotNull T type, int quantity) {
		return 0;
	}

	@Override
	public boolean isEmpty(@Nullable Transaction transaction) {
		return this.extractable.isEmpty(transaction);
	}

	@Override
	public void clear(@Nullable Transaction transaction) {
		this.extractable.clear(transaction);
	}

	@Override
	public boolean isFull(@Nullable Transaction transaction) {
		return true;
	}

	@Override
	public long getVersion() {
		return this.extractable.getVersion();
	}

	@Override
	public boolean supportsExtraction() {
		return true;
	}

	@Override
	public boolean supportsInsertion() {
		return false;
	}

	@Override
	public @Nullable Object get(Access<?> access) {
		return access == Participants.DIRECT_WRAPPERS_EXTRACTABLE ? (T) this.extractable : null;
	}
}
