package io.github.astrarre.transfer.internal;

import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.access.v0.api.func.Returns;
import io.github.astrarre.access.v0.api.provider.Provider;
import io.github.astrarre.transfer.v0.api.Insertable;
import io.github.astrarre.transfer.v0.api.Participant;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import io.github.astrarre.transfer.v0.api.util.Participants;
import org.jetbrains.annotations.Nullable;

public final class InsertableParticipant<T> implements Participant<T>, Provider {
	public final Insertable<T> insertable;

	public InsertableParticipant(Insertable<T> insertable) {this.insertable = insertable;}

	@Override
	public void extract(@Nullable Transaction transaction, Insertable<T> insertable) {}

	@Override
	public int extract(@Nullable Transaction transaction, T type, int quantity) {return 0;}

	@Override
	public int insert(@Nullable Transaction transaction, T type, int quantity) {
		return this.insertable.insert(transaction, type, quantity);
	}

	@Override
	public boolean isEmpty(@Nullable Transaction transaction) {
		return true;
	}

	@Override
	public void clear(@Nullable Transaction transaction) {}

	@Override
	public boolean isFull(@Nullable Transaction transaction) {
		return this.insertable.isFull(transaction);
	}

	@Override
	public long getVersion() {
		return this.insertable.getVersion();
	}

	@Override
	public boolean supportsExtraction() {
		return false;
	}

	@Override
	public boolean supportsInsertion() {
		return true;
	}

	@Override
	public <E> @Nullable E get(Access<? extends Returns<E>, E> access) {
		return Participants.DIRECT_WRAPPERS_INSERTABLE == access ? (E) this.insertable : null;
	}
}
