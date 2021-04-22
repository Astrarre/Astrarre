package io.github.astrarre.transfer.v0.api.participants;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.github.astrarre.transfer.v0.api.participants.array.ArrayParticipant;
import io.github.astrarre.transfer.v0.api.participants.array.Slot;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import io.github.astrarre.transfer.v0.api.transaction.keys.DiffKey;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import org.jetbrains.annotations.Nullable;

/**
 * A default array participant implementation
 * @param <T>
 */
public class ContainerParticipant<T> implements ArrayParticipant<T> {
	protected final DiffKey.Array<T> type;
	protected final DiffKey.Array<Integer> quantity;

	public ContainerParticipant(int size) {
		this.type = new DiffKey.Array<>(new ArrayList<>(size));
		this.quantity = new DiffKey.Array<>(new IntArrayList(size));
	}

	@Override
	public List<Slot<T>> getSlots() {
		return new AbstractList<Slot<T>>() {
			@Override
			public Slot<T> get(int index) {
				return new SlotImpl<>(ContainerParticipant.this.type, ContainerParticipant.this.quantity, index);
			}

			@Override
			public int size() {
				return ContainerParticipant.this.type.get(Transaction.GLOBAL).size();
			}
		};
	}

	public static class SlotImpl<T> implements Slot<T> {
		protected final DiffKey.Array<T> type;
		protected final DiffKey.Array<Integer> quantity;
		protected final int index;

		public SlotImpl(DiffKey.Array<T> type, DiffKey.Array<Integer> quantity, int index) {
			this.type = type;
			this.quantity = quantity;
			this.index = index;}

		@Override
		public T getKey(@Nullable Transaction transaction) {
			return this.type.get(transaction).get(this.index);
		}

		@Override
		public int getQuantity(@Nullable Transaction transaction) {
			return this.quantity.get(transaction).get(this.index);
		}

		@Override
		public boolean set(@Nullable Transaction transaction, T key, int quantity) {
			this.type.get(transaction).set(this.index, key);
			this.quantity.get(transaction).set(this.index, quantity);
			return false;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (!(o instanceof SlotImpl)) {
				return false;
			}

			SlotImpl<?> slot = (SlotImpl<?>) o;

			if (this.index != slot.index) {
				return false;
			}
			if (!Objects.equals(this.type, slot.type)) {
				return false;
			}
			return Objects.equals(this.quantity, slot.quantity);
		}

		@Override
		public int hashCode() {
			int result = this.type != null ? this.type.hashCode() : 0;
			result = 31 * result + (this.quantity != null ? this.quantity.hashCode() : 0);
			result = 31 * result + this.index;
			return result;
		}
	}
}
