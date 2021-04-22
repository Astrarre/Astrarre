package io.github.astrarre.transfer.v0.lba.adapters;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.misc.LimitedConsumer;
import alexiil.mc.lib.attributes.misc.Reference;
import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.transfer.v0.api.Participant;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;

import net.minecraft.item.ItemStack;

public class LBAItemApiApiContext implements Reference<ItemStack>, LimitedConsumer<ItemStack> {
	public final Participant<ItemKey> participant;
	public ItemStack current;
	public LBAItemApiApiContext(Participant<ItemKey> participant, ItemStack current) {
		this.participant = participant;
		this.current = current;
	}

	@Override
	public boolean offer(ItemStack object, Simulation simulation) {
		return this.set(object, simulation.isAction(), false);
	}

	@Override
	public ItemStack get() {
		return this.current;
	}

	@Override
	public boolean set(ItemStack value) {
		return this.set(value, true, true);
	}

	@Override
	public boolean isValid(ItemStack value) {
		return this.set(value, false, true);
	}

	protected boolean set(ItemStack value, boolean act, boolean extract) {
		try(Transaction transaction = Transaction.create(act)) {
			if(extract) {
				int count = this.participant.extract(transaction, ItemKey.of(this.current), this.current.getCount());
				if (count != this.current.getCount()) {
					transaction.abort();
					return false;
				}
			}

			int insert = this.participant.insert(transaction, ItemKey.of(value), value.getCount());
			if(insert != value.getCount()) {
				transaction.abort();
				return false;
			}

			return true;
		}
	}
}
