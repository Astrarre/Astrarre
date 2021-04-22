package io.github.astrarre.transfer.v0.api.participants.array;

import java.util.List;

import io.github.astrarre.transfer.v0.api.Insertable;
import io.github.astrarre.transfer.v0.api.Participant;
import io.github.astrarre.transfer.v0.api.ReplacingParticipant;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ArrayParticipant<T> extends Participant<T> {
	/**
	 * @return a view-only list of the containers's slots
	 */
	List<Slot<T>> getSlots();

	@Override
	default void extract(@Nullable Transaction transaction, Insertable<T> insertable) {
		for (Slot<T> slot : this.getSlots()) {
			slot.extract(transaction, insertable);
			if(insertable.isFull(transaction)) {
				return;
			}
		}
	}

	@Override
	default int extract(@Nullable Transaction transaction, @NotNull T type, int quantity) {
		int count = 0;
		for (Slot<T> slot : this.getSlots()) {
			int extracted = slot.extract(transaction, type, quantity);
			count += extracted;
			quantity -= extracted;
			if(quantity == 0) {
				break;
			}
		}
		return count;
	}

	@Override
	default int insert(@Nullable Transaction transaction, @NotNull T type, int quantity) {
		int count = 0;
		for (Slot<T> slot : this.getSlots()) {
			int inserted = slot.insert(transaction, type, quantity);
			count += inserted;
			quantity -= inserted;
			if(quantity == 0) {
				break;
			}
		}
		return count;
	}

	default ReplacingParticipant<T> getSlotReplacingParticipant(int slot) {
		return new ReplacingParticipant<T>() {
			final Slot<T> prioritySlot = ArrayParticipant.this.getSlots().get(slot);

			@Override
			public boolean replace(@Nullable Transaction transaction, T target, int targetAmount, T replacement, int replacementAmount) {
				if(target.equals(this.prioritySlot.getKey(transaction))) {
					int quantity = this.prioritySlot.getQuantity(transaction);
					if(targetAmount == quantity) {
						return this.prioritySlot.set(transaction, replacement, replacementAmount);
					} else if(targetAmount < quantity) {
						try (Transaction action = Transaction.create(false)) {
							if (this.prioritySlot.extract(transaction, target, targetAmount) == targetAmount) {
								if(this.insert(transaction, replacement, replacementAmount) == replacementAmount) {
									action.commit();
									return true;
								}
							}
							return false;
						}
					}
				}
				return ReplacingParticipant.super.replace(transaction, target, targetAmount, replacement, replacementAmount);
			}

			@Override
			public void extract(@Nullable Transaction transaction, Insertable<T> insertable) {
				for (Slot<T> slot : ArrayParticipant.this.getSlots()) {
					if(slot.equals(this.prioritySlot)) {
						break; // low priority
					}
					slot.extract(transaction, insertable);
					if(insertable.isFull(transaction)) {
						return;
					}
				}
				if(!insertable.isFull(transaction)) {
					this.prioritySlot.extract(transaction, insertable);
				}
			}

			@Override
			public int extract(@Nullable Transaction transaction, @NotNull T type, int quantity) {
				int count = 0;
				for (Slot<T> slot : ArrayParticipant.this.getSlots()) {
					if(slot.equals(this.prioritySlot)) {
						break; // low priority
					}
					int extracted = slot.extract(transaction, type, quantity);
					count += extracted;
					quantity -= extracted;
					if(quantity == 0) {
						break;
					}
				}

				if(quantity != 0) {
					count += this.prioritySlot.extract(transaction, type, quantity);
				}
				return count;
			}

			@Override
			public int insert(@Nullable Transaction transaction, @NotNull T type, int quantity) {
				int count = 0;
				for (Slot<T> slot : ArrayParticipant.this.getSlots()) {
					if(slot.equals(this.prioritySlot)) {
						break; // low priority
					}
					int inserted = slot.insert(transaction, type, quantity);
					count += inserted;
					quantity -= inserted;
					if(quantity == 0) {
						break;
					}
				}

				if(quantity != 0) {
					count += this.prioritySlot.insert(transaction, type, quantity);
				}
				return count;
			}
		};
	}
}
