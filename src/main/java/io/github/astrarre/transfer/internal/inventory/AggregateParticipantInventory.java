package io.github.astrarre.transfer.internal.inventory;

import java.util.Set;

import io.github.astrarre.itemview.v0.api.item.ItemKey;
import io.github.astrarre.transfer.v0.api.Participant;
import io.github.astrarre.transfer.v0.api.participants.AggregateParticipant;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class AggregateParticipantInventory implements Inventory {
	protected final AggregateParticipant<ItemKey> participant;
	protected final InternalItemSlotParticipant access = new InternalItemSlotParticipant();

	public AggregateParticipantInventory(AggregateParticipant<ItemKey> participant) {
		this.participant = participant;
	}

	@Override
	public int size() {
		return this.participant.size();
	}

	@Override
	public boolean isEmpty() {
		return this.participant.isEmpty(null);
	}

	@Override
	public ItemStack getStack(int slot) {
		Participant<ItemKey> participant = this.participant.getParticipant(slot);
		try (Transaction transaction = new Transaction(false)) {
			participant.extract(transaction, this.access);
			return this.access.getType(transaction).createItemStack(this.access.getQuantity(transaction));
		}
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		Participant<ItemKey> participant = this.participant.getParticipant(slot);
		int oldMax = this.access.getMax(null);
		// cap amount
		this.access.setMax(amount);

		participant.extract(null, this.access);
		ItemStack stack = this.access.getType(null).createItemStack(this.access.getQuantity(null));
		// reset
		this.access.clear(null);
		this.access.setMax(oldMax);
		return stack;
	}

	@Override
	public ItemStack removeStack(int slot) {
		return this.removeStack(slot, Integer.MAX_VALUE);
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		this.removeStack(slot);
		Participant<ItemKey> participant = this.participant.getParticipant(slot);
		// todo what to do with voided items?
		participant.insert(null, ItemKey.of(stack), stack.getCount());
	}

	@Override
	public void markDirty() {}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return true;
	}

	@Override
	public void clear() {
		this.participant.clear(null);
	}

	@Override
	public int getMaxCountPerStack() {
		return Integer.MAX_VALUE;
	}

	@Override
	public void onOpen(PlayerEntity player) {}

	@Override
	public void onClose(PlayerEntity player) {}

	@Override
	public boolean isValid(int slot, ItemStack stack) {
		Participant<ItemKey> participant = this.participant.getParticipant(slot);
		if(participant.isFull(null) && participant.supportsInsertion()) {
			try(Transaction transaction = new Transaction(false)) {
				return participant.insert(transaction, ItemKey.of(stack), stack.getCount()) == stack.getCount();
			}
		}
		return false;
	}

	@Override
	public int count(Item item) {
		return this.participant.extract(null, ItemKey.of(item), Integer.MAX_VALUE);
	}

	@Override
	public boolean containsAny(Set<Item> items) {
		return false;
	}
}
