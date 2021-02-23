package io.github.astrarre.transfer.v0.api.participants;

import io.github.astrarre.transfer.v0.api.Insertable;
import io.github.astrarre.transfer.v0.api.Participant;
import io.github.astrarre.transfer.v0.api.transaction.Key;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import io.github.astrarre.transfer.v0.api.transaction.keys.ObjectKeyImpl;
import io.github.astrarre.transfer.v0.api.transaction.keys.generated.IntKeyImpl;
import org.jetbrains.annotations.Nullable;

public class ObjectVolume<T> implements Participant<T> {
	protected final T empty;
	protected final Key.Object<T> type;
	protected final Key.Int quantity;

	public ObjectVolume(T empty) {
		this(empty, empty, 0);
	}

	public ObjectVolume(T empty, T object, int quantity) {
		this.empty = empty;
		if (object == this.empty && quantity != 0) {
			throw new IllegalArgumentException("cannot have " + quantity + " units of EMPTY!");
		} else if (quantity < 0) {
			throw new IllegalArgumentException("Cannot have negative units of " + object);
		}

		if (quantity == 0) {
			object = empty;
		}

		this.type = new ObjectKeyImpl<>(object);
		this.quantity = new IntKeyImpl(quantity);
	}

	public T getType(@Nullable Transaction transaction) {
		return this.type.get(transaction);
	}

	public int getQuantity(@Nullable Transaction transaction) {
		return this.quantity.get(transaction);
	}

	@Override
	public int insert(@Nullable Transaction transaction, T type, int quantity) {
		if (quantity == 0) {
			return 0;
		}

		T fluid = this.type.get(transaction);
		if (fluid == this.empty || fluid == type) {
			if (fluid != type) {
				this.type.set(transaction, type);
			}

			this.quantity.set(transaction, this.quantity.get(transaction) + quantity);
			return quantity;
		}
		return 0;
	}

	@Override
	public void extract(@Nullable Transaction transaction, Insertable<T> insertable) {
		int oldLevel = this.quantity.get(transaction);
		int amount = insertable.insert(transaction, this.type.get(transaction), oldLevel);
		int newLevel = oldLevel - amount;
		this.quantity.set(transaction, newLevel);
		if (newLevel == 0) {
			this.type.set(transaction, this.empty);
		}
	}

	@Override
	public int extract(@Nullable Transaction transaction, T type, int quantity) {
		if (quantity == 0) {
			return 0;
		}

		if (this.type.get(transaction) == type) {
			int oldLevel = this.quantity.get(transaction);
			int toExtract = Math.min(oldLevel, quantity);
			int newLevel = oldLevel - toExtract;
			if (newLevel == 0) {
				this.type.set(transaction, this.empty);
			}
			return toExtract;
		}

		return 0;
	}

	@Override
	public boolean isEmpty(@Nullable Transaction transaction) {
		return this.type.get(transaction) == this.empty;
	}

	@Override
	public void clear(@Nullable Transaction transaction) {
		this.type.set(transaction, this.empty);
		this.quantity.set(transaction, 0);
	}
}
