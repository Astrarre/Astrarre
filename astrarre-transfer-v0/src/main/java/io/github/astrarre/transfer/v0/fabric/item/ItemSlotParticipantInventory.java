package io.github.astrarre.transfer.v0.fabric.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.transfer.internal.access.ItemStackAccess;
import io.github.astrarre.transfer.internal.participantInventory.SetMatchingInsertable;
import io.github.astrarre.transfer.v0.api.item.ItemSlotParticipant;
import io.github.astrarre.transfer.v0.api.participants.ObjectVolume;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * this Inventory compat is for lists of ItemSlotParticipants specifically, this allows for complete and perfect compat
 */
public class ItemSlotParticipantInventory implements Inventory {
	public final List<ItemSlotParticipant> participants;

	public ItemSlotParticipantInventory() {
		this(new ArrayList<>());
	}

	public ItemSlotParticipantInventory(List<ItemSlotParticipant> participants) {
		this.participants = participants;
	}

	public void add(ItemSlotParticipant volume) {
		this.participants.add(volume);
	}

	@Override
	public int size() {
		return this.participants.size();
	}

	@Override
	public boolean isEmpty() {
		for (ItemSlotParticipant participant : this.participants) {
			if (!participant.isEmpty(Transaction.GLOBAL)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public ItemStack getStack(int slot) {
		ItemSlotParticipant key = this.participants.get(slot);
		return key.type.get(Transaction.GLOBAL);
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		ItemSlotParticipant key = this.participants.get(slot);
		if (key.isEmpty(Transaction.GLOBAL) || amount <= 0) {
			return ItemStack.EMPTY;
		}
		ItemStack item = key.type.get(Transaction.GLOBAL).copy();
		int quantity = key.extract(Transaction.GLOBAL, ItemKey.of(item), amount);
		item.setCount(quantity);
		return item;
	}

	@Override
	public ItemStack removeStack(int slot) {
		return this.removeStack(slot, Integer.MAX_VALUE);
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		ItemSlotParticipant key = this.participants.get(slot);
		key.type.set(Transaction.GLOBAL, stack);
	}

	@Override
	public void markDirty() {
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return true;
	}

	@Override
	public boolean isValid(int slot, ItemStack stack) {
		try (Transaction t = Transaction.create(false)) {
			ItemSlotParticipant key = this.participants.get(slot);
			key.clear(t);
			int count = key.insert(t, ItemKey.of(stack), stack.getCount());
			return count == stack.getCount();
		}
	}

	@Override
	public int count(Item item) {
		try (Transaction t = Transaction.create(false)) {
			SetMatchingInsertable insertable = new SetMatchingInsertable(Collections.singleton(item), 1);
			for (ItemSlotParticipant participant : this.participants) {
				participant.extract(t, insertable);
			}
			return insertable.found.get(t);
		}
	}

	@Override
	public boolean containsAny(Set<Item> items) {
		try (Transaction t = Transaction.create(false)) {
			SetMatchingInsertable insertable = new SetMatchingInsertable(items, 1);
			for (ItemSlotParticipant participant : this.participants) {
				participant.extract(t, insertable);
			}
			return insertable.isFull(t);
		}
	}

	@Override
	public void clear() {
		for (ItemSlotParticipant participant : this.participants) {
			participant.clear(Transaction.GLOBAL);
		}
	}
}
