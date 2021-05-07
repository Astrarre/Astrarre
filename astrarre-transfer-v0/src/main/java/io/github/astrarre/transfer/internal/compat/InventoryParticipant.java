package io.github.astrarre.transfer.internal.compat;

import java.util.AbstractList;
import java.util.List;

import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.access.v0.api.provider.Provider;
import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.transfer.v0.api.participants.array.ArrayParticipant;
import io.github.astrarre.transfer.v0.api.participants.array.Slot;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import io.github.astrarre.transfer.v0.api.transaction.keys.DiffKey;
import io.github.astrarre.transfer.v0.fabric.inventory.InventoryList;
import io.github.astrarre.transfer.v0.fabric.participants.FabricParticipants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class InventoryParticipant implements ArrayParticipant<ItemKey>, Provider {
	public final DiffKey.Array<ItemStack> array;
	public final Inventory inventory;

	public InventoryParticipant(Inventory inventory) {
		this.array = new DiffKey.Array<>(new InventoryList(inventory));
		this.inventory = inventory;
	}

	@Override
	public List<Slot<ItemKey>> getSlots() {
		return new AbstractList<Slot<ItemKey>>() {
			@Override
			public Slot<ItemKey> get(int index) {
				return InventoryParticipant.this.createSlot(index);
			}

			@Override
			public int size() {
				return InventoryParticipant.this.array.get(null).size();
			}
		};
	}

	@Override
	public @Nullable Object get(Access<?> access) {
		if(access == FabricParticipants.TO_INVENTORY) {
			return this.inventory;
		}
		return null;
	}

	protected Slot<ItemKey> createSlot(int index) {
		return new SlotImpl(this, index);
	}

	public static class SlotImpl implements Slot<ItemKey> {
		public final InventoryParticipant participant;
		public final int index;

		public SlotImpl(InventoryParticipant participant, int index) {
			this.participant = participant;
			this.index = index;
		}

		@Override
		public ItemKey getKey(@Nullable Transaction transaction) {
			return ItemKey.of(this.participant.array.get(transaction).get(this.index));
		}

		@Override
		public int getQuantity(@Nullable Transaction transaction) {
			return this.participant.array.get(transaction).get(this.index).getCount();
		}

		@Override
		public int insert(@Nullable Transaction transaction, @NotNull ItemKey key, int quantity) {
			int current = this.participant.array.get(transaction).get(this.index).getCount();
			return Slot.super.insert(transaction, key, Math.min(this.participant.inventory.getMaxCountPerStack() - current, quantity));
		}

		@Override
		public boolean set(@Nullable Transaction transaction, ItemKey key, int quantity) {
			if(this.participant.inventory.isValid(this.index, key.createItemStack(quantity)) && this.participant.inventory.getMaxCountPerStack() >= quantity) {
				this.participant.array.get(transaction).set(this.index, key.createItemStack(quantity));
				return true;
			}
			return false;
		}

		@Override
		public boolean isFull(@Nullable Transaction transaction) {
			return this.getQuantity(transaction) >= this.participant.array.get(transaction).get(this.index).getMaxCount();
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (!(o instanceof SlotImpl)) {
				return false;
			}

			SlotImpl slot = (SlotImpl) o;
			return this.index == slot.index && slot.participant == this.participant;
		}

		@Override
		public int hashCode() {
			return this.index;
		}
	}
}
