package io.github.astrarre.transfer.v0.api;

import io.github.astrarre.transfer.internal.TransferInternal;
import io.github.astrarre.transfer.v0.api.participants.DelegateParticipant;
import io.github.astrarre.transfer.internal.participants.ExtractableParticipant;
import io.github.astrarre.transfer.internal.participants.InsertableParticipant;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @see Droplet
 */
public interface Participant<T> extends Extractable<T>, Insertable<T> {
	/**
	 * {@inheritDoc}
	 */
	@Override
	default long getVersion() {
		return TransferInternal.version++;
	}
	default boolean supportsExtraction() {
		return true;
	}
	default boolean supportsInsertion() {
		return true;
	}

	default DelegateParticipant<T> delegate() {
		return () -> this;
	}

	static <T> Participant<T> of(Extractable<T> extractable) {
		return new ExtractableParticipant<>(extractable);
	}

	static <T> Participant<T> of(Insertable<T> insertable) {
		return new InsertableParticipant<>(insertable);
	}

	/**
	 * creates a participant for the extractable and insertable
	 */
	static <T> Participant<T> of(Extractable<T> extractable, Insertable<T> insertable) {
		return new Participant<T>() {
			@Override
			public void extract(@Nullable Transaction transaction, Insertable<T> insertable) {
				extractable.extract(transaction, insertable);
			}

			@Override
			public int extract(@Nullable Transaction transaction, @NotNull T type, int quantity) {
				return extractable.extract(transaction, type, quantity);
			}

			@Override
			public int insert(@Nullable Transaction transaction, @NotNull T type, int quantity) {
				return insertable.insert(transaction, type, quantity);
			}

			@Override
			public long getVersion() {
				long a = extractable.getVersion();
				a = 31 * a + insertable.getVersion();
				return a;
			}

			@Override
			public boolean isFull(@Nullable Transaction transaction) {
				return insertable.isFull(transaction);
			}

			@Override
			public boolean isEmpty(@Nullable Transaction transaction) {
				return extractable.isEmpty(transaction);
			}
		};
	}

	/**
	 * @see #of(Extractable, Insertable)
	 */
	static <T> Participant<T> of(Insertable<T> insertable, Extractable<T> extractable) {
		return of(extractable, insertable);
	}
}
