package io.github.astrarre.transfer.internal;

import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.access.v0.api.func.Returns;
import io.github.astrarre.access.v0.fabric.provider.Provider;
import io.github.astrarre.transfer.v0.api.Extractable;
import io.github.astrarre.transfer.v0.api.Insertable;
import io.github.astrarre.transfer.v0.api.Participant;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import io.github.astrarre.transfer.v0.api.util.Participants;
import org.jetbrains.annotations.Nullable;

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
	public int extract(@Nullable Transaction transaction, T type, int quantity) {
		return this.extractable.extract(transaction, type, quantity);
	}

	@Override
	public int insert(@Nullable Transaction transaction, T type, int quantity) {
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
	public <E> @Nullable E get(Access<? extends Returns<E>, E> access) {
		return access == Participants.DIRECT_WRAPPERS_EXTRACTABLE ? (E) this.extractable : null;
	}
}
