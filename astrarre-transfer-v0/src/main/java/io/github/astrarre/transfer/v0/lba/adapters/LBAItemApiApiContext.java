package io.github.astrarre.transfer.v0.lba.adapters;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.misc.LimitedConsumer;
import alexiil.mc.lib.attributes.misc.Reference;
import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.transfer.v0.api.Participant;
import io.github.astrarre.transfer.v0.api.ReplacingParticipant;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;

import net.minecraft.item.ItemStack;

public class LBAItemApiApiContext implements Reference<ItemStack>, LimitedConsumer<ItemStack> {
	public final ReplacingParticipant<ItemKey> participant;
	public ItemStack current;
	public LBAItemApiApiContext(ReplacingParticipant<ItemKey> participant, ItemStack current) {
		this.participant = participant;
		this.current = current;
	}

	@Override
	public boolean offer(ItemStack object, Simulation simulation) {
		try(Transaction transaction = Transaction.create(simulation.isAction())) {
			int insert = this.participant.insert(transaction, ItemKey.of(object), object.getCount());
			if(insert != object.getCount()) {
				transaction.abort();
				return false;
			}
			return true;
		}
	}

	@Override
	public ItemStack get() {
		return this.current;
	}

	@Override
	public boolean set(ItemStack value) {
		return this.set(value, true);
	}

	@Override
	public boolean isValid(ItemStack value) {
		return this.set(value, false);
	}

	protected boolean set(ItemStack value, boolean act) {
		boolean toReturn;
		try(Transaction transaction = Transaction.create(act)) {
			if(this.participant.replace(transaction, ItemKey.of(this.current), this.current.getCount(), ItemKey.of(value), value.getCount())) {
				if(act) {
					this.current = value;
				}
				toReturn = true;
			} else {
				toReturn = false;
			}
		}
		return toReturn;
	}
}
