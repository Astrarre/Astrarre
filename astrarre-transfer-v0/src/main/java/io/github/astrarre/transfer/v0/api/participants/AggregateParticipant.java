package io.github.astrarre.transfer.v0.api.participants;

import java.util.Collection;

import io.github.astrarre.transfer.v0.api.Insertable;
import io.github.astrarre.transfer.v0.api.Participant;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;

public class AggregateParticipant<T> implements Participant<T> {
	private final Participant<T>[] participants;

	public AggregateParticipant(Participant<T>[] participants) {
		this.participants = participants;
	}

	public AggregateParticipant(Collection<Participant<T>> participants) {
		this(participants.toArray(new Participant[participants.size()]));
	}

	@Override
	public void extract(Transaction transaction, Insertable<T> insertable) {
		for (Participant<T> participant : this.participants) {
			if(insertable.isFull(transaction)) {
				return;
			}
			participant.extract(transaction, insertable);
		}
	}

	@Override
	public int extract(Transaction transaction, T type, int amount) {
		return this.act(transaction, type, amount, true);
	}

	@Override
	public int insert(Transaction transaction, T type, int amount) {
		return this.act(transaction, type, amount, false);
	}

	protected int act(Transaction transaction, T type, int amount, boolean extract) {
		int counter = 0;
		for (Participant<T> participant : this.participants) {
			if(amount <= 0) {
				break;
			}
			int quantity = extract ? participant.extract(transaction, type, amount) : participant.insert(transaction, type, amount);
			amount -= quantity;
			counter += amount;
		}

		return counter;
	}
}
