package io.github.astrarre.transfer.internal.participantInventory;

import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.transfer.internal.access.ItemStackAccess;
import io.github.astrarre.transfer.v0.api.participants.array.ArrayParticipant;
import io.github.astrarre.transfer.v0.api.participants.array.Slot;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class ArrayParticipantInventory implements Inventory {
	protected final ArrayParticipant<ItemKey> participant;

	public ArrayParticipantInventory(ArrayParticipant<ItemKey> participant) {
		this.participant = participant;
	}

	@Override
	public int size() {
		return this.participant.getSlots().size();
	}

	@Override
	public boolean isEmpty() {
		for (Slot<ItemKey> slot : this.participant.getSlots()) {
			if(!slot.isEmpty(Transaction.GLOBAL)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public ItemStack getStack(int index) {
		Slot<ItemKey> slot = this.participant.getSlots().get(index);
		ItemStack stack = slot.getKey(Transaction.GLOBAL).createItemStack(slot.getQuantity(Transaction.GLOBAL));
		this.watchStack(stack, index);
		return stack;
	}

	@Override
	public ItemStack removeStack(int index, int amount) {
		Slot<ItemKey> slot = this.participant.getSlots().get(index);
		ItemKey key = slot.getKey(Transaction.GLOBAL);
		return key.createItemStack(slot.extract(Transaction.GLOBAL, amount));
	}

	@Override
	public ItemStack removeStack(int slot) {
		return this.removeStack(slot, Integer.MAX_VALUE);
	}

	@Override
	public void setStack(int index, ItemStack stack) {
		Slot<ItemKey> slot = this.participant.getSlots().get(index);
		slot.set(Transaction.GLOBAL, ItemKey.of(stack), stack.getCount());
		this.watchStack(stack, index);
	}

	@Override
	public void markDirty() {
		// todo check compound tag
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return true;
	}

	@Override
	public void clear() {
		this.participant.clear(Transaction.GLOBAL);
	}

	private static int counter = 0;
	/**
	 * allows mutations in the ItemStack to be reflected in the participant (nbt stuff still doesn't tho)
	 */
	private void watchStack(ItemStack stack, int slotIndex) {
		int[] lastCount = {stack.getCount()};
		ItemStackAccess.of(stack).astrarre_onChange(i -> {
			int count = i.getCount();
			int oldCount = lastCount[0];
			if (count == oldCount) {
				return;
			}

			Slot<ItemKey> slot = this.participant.getSlots().get(slotIndex);
			try (Transaction transaction = Transaction.create(true)) {
				if (!slot.set(transaction, ItemKey.of(i), i.getCount())) {
					// duplicated items, reject the stack count change
					transaction.abort();
					i.setCount(oldCount);
					if (counter % 10 == 0) {
						ParticipantInventory.LOGGER.warn(
								"Denying ItemStack#setCount modification in an attempt to prevent duplication! (This warning is only logged every 10" +
								" times this happens)");
					}
					counter++;
				}
			}
		});
	}
}
