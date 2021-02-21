package io.github.astrarre.transfer.internal.inventory;

import io.github.astrarre.transfer.v0.api.participants.item.ItemSlotParticipant;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;

import net.minecraft.item.ItemStack;

public class InternalItemSlotParticipant extends ItemSlotParticipant {
	public void setMax(int max) {
		this.max = max;
	}

	public ItemStack getItemStack(Transaction transaction) {
		return this.getType(transaction).createItemStack(this.getQuantity(transaction));
	}
}
