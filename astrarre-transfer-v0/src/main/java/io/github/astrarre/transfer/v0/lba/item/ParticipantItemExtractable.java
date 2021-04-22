package io.github.astrarre.transfer.v0.lba.item;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.ItemExtractable;
import alexiil.mc.lib.attributes.item.filter.ExactItemFilter;
import alexiil.mc.lib.attributes.item.filter.ExactItemStackFilter;
import alexiil.mc.lib.attributes.item.filter.ItemFilter;
import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.transfer.v0.api.Participant;
import io.github.astrarre.transfer.v0.api.item.ItemSlotParticipant;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ParticipantItemExtractable implements ItemExtractable {
	public final Participant<ItemKey> participant;

	public ParticipantItemExtractable(Participant<ItemKey> participant) {
		this.participant = participant;
	}

	@Override
	public ItemStack attemptExtraction(ItemFilter filter, int count, Simulation simulation) {
		try(Transaction transaction = Transaction.create(simulation.isAction())) {
			if(filter instanceof ExactItemFilter) {
				Item item = ((ExactItemFilter) filter).item;
				return new ItemStack(item, this.participant.extract(transaction, ItemKey.of(item), count));
			}

			if(filter instanceof ExactItemStackFilter) {
				ItemStack stack = ((ExactItemStackFilter) filter).stack.copy();
				stack.setCount(this.participant.extract(transaction, ItemKey.of(stack), count));
				return stack;
			}

			ItemSlotParticipant participant = new ItemSlotParticipant() {
				@Override
				public int getMax(ItemKey key) {
					return Math.min(super.getMax(key), count);
				}
			};
			// todo respect amount
			this.participant.extract(transaction, new ItemFilterFilteringInsertable(filter, participant));
			return participant.getStack(transaction);
		}
	}
}
