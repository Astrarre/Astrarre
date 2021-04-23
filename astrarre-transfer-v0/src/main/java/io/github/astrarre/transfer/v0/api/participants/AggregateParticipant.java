package io.github.astrarre.transfer.v0.api.participants;

import java.util.Arrays;
import java.util.Iterator;

import com.google.common.collect.Iterables;
import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.access.v0.api.provider.Provider;
import io.github.astrarre.transfer.v0.api.Insertable;
import io.github.astrarre.transfer.v0.api.Participants;
import io.github.astrarre.transfer.v0.fabric.participants.FabricParticipants;
import io.github.astrarre.util.v0.api.collection.DualIterator;
import io.github.astrarre.transfer.v0.api.Participant;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import it.unimi.dsi.fastutil.HashCommon;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A participant that delegates to other participants
 * @implNote {@link #iterator()}
 */
public class AggregateParticipant<T> implements Participant<T>, Iterable<Participant<T>>, Provider {
	private final Iterable<Participant<T>> participants;

	public static <T> AggregateParticipant<T> merge(Participant<T> a, Participant<T> b) {
		return new AggregateParticipant<>(() -> new DualIterator<>(a, b));
	}

	public static <T> AggregateParticipant<T> merge(Iterable<Participant<T>> participants) {
		return new AggregateParticipant<>(() -> iterate(participants.iterator()));
	}

	public AggregateParticipant(Participant<T>[] participants) {
		this.participants = Arrays.asList(participants);
	}

	/**
	 * @param participants a list is the prefered and most effecient datastructure for this
	 */
	public AggregateParticipant(Iterable<Participant<T>> participants) {
		this.participants = participants;
	}

	@Override
	public void extract(Transaction transaction, Insertable<T> insertable) {
		for (Participant<T> participant : this.participants) {
			if(insertable.isFull(transaction)) {
				return;
			}

			if(participant.isEmpty(transaction) || !participant.supportsExtraction()) {
				continue;
			}
			participant.extract(transaction, insertable);
		}
	}

	@Override
	public int extract(Transaction transaction, @NotNull T type, int quantity) {
		return this.act(transaction, type, quantity, true);
	}

	@Override
	public int insert(Transaction transaction, @NotNull T type, int quantity) {
		return this.act(transaction, type, quantity, false);
	}

	protected int act(Transaction transaction, T type, int amount, boolean extract) {
		int counter = 0;
		for (Participant<T> participant : this.participants) {
			if(amount <= 0) {
				break;
			}

			if(extract && (participant.isEmpty(transaction) || !participant.supportsExtraction())) {
				continue;
			} else if(participant.isFull(transaction) || !participant.supportsInsertion()) {
				continue;
			}

			int quantity = extract ? participant.extract(transaction, type, amount) : participant.insert(transaction, type, amount);
			amount -= quantity;
			counter += amount;
		}

		return counter;
	}

	public int size() {
		return Iterables.size(this.participants);
	}

	public Participant<T> getParticipant(int index) {
		return Iterables.get(this.participants, index);
	}

	@Override
	public boolean supportsExtraction() {
		return Iterables.any(this.participants, Participant::supportsExtraction);
	}

	@Override
	public boolean supportsInsertion() {
		return Iterables.any(this.participants, Participant::supportsInsertion);
	}

	@Override
	public long getVersion() {
		long version = 1;
		for (Participant<T> participant : this.participants) {
			version = version * 31 + HashCommon.murmurHash3(participant.getVersion());
		}
		return version;
	}

	@Override
	public boolean isEmpty(@Nullable Transaction transaction) {
		for (Participant<T> participant : this.participants) {
			if(!participant.isEmpty(transaction)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean isFull(@Nullable Transaction transaction) {
		for (Participant<T> participant : this.participants) {
			if(!participant.isFull(transaction)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * iterate over a <b>flattened</b> version of this container.
	 * If you extend AggregateParticipant to filter or add custom logic, you must extend this method and either return a singleton iterator of yourself, or break your collective logic into individual operations
	 */
	@NotNull
	@Override
	public Iterator<Participant<T>> iterator() {
		return AggregateParticipant.iterate(this.participants.iterator());
	}

	/**
	 * @return a new iterator that flattens any aggregate participants
	 */
	protected static <T> Iterator<Participant<T>> iterate(Iterator<Participant<T>> iterable) {
		return new Iterator<Participant<T>>() {
			final Iterator<Participant<T>> current = iterable;
			Iterator<Participant<T>> temp;

			@Override
			public boolean hasNext() {
				return (this.temp != null && this.temp.hasNext()) || this.current.hasNext();
			}

			@Override
			public Participant<T> next() {
				if (this.temp != null && this.temp.hasNext()) {
					return this.temp.next();
				}

				Participant<T> current = this.current.next();
				if(current instanceof AggregateParticipant) {
					Iterator<Participant<T>> temp = ((AggregateParticipant) current).iterator();
					if(temp.hasNext()) {
						this.temp = temp;
						return temp.next();
					}
				}
				return current;
			}
		};
	}

	@Override
	public @Nullable Object get(Access<?> access) {
		return access == Participants.AGGREGATE_WRAPPERS ? this.participants : null;
	}
}
