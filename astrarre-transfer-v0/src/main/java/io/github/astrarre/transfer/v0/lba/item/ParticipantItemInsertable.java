package io.github.astrarre.transfer.v0.lba.item;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.ItemInsertable;
import alexiil.mc.lib.attributes.item.filter.ExactItemFilter;
import alexiil.mc.lib.attributes.item.filter.ExactItemStackFilter;
import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.transfer.v0.api.Participant;
import io.github.astrarre.transfer.v0.api.item.ItemSlotParticipant;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ParticipantItemInsertable implements ItemInsertable {

	public final Participant<ItemKey> participant;

	public ParticipantItemInsertable(Participant<ItemKey> participant) {
		this.participant = participant;
	}

	@Override
	public ItemStack attemptInsertion(ItemStack stack, Simulation simulation) {
		try(Transaction transaction = Transaction.create(simulation.isAction())) {
			ItemStack copy = stack.copy();
			int inserted = this.participant.insert(transaction, ItemKey.of(stack), stack.getCount());
			copy.decrement(inserted);
			return copy;
		}
	}
}
