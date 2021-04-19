package io.github.astrarre.transfer.v0.lba;

import alexiil.mc.lib.attributes.misc.LimitedConsumer;
import alexiil.mc.lib.attributes.misc.Reference;
import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.transfer.v0.api.Insertable;
import io.github.astrarre.transfer.v0.api.Participant;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import io.github.astrarre.transfer.v0.api.transaction.keys.ObjectKeyImpl;
import io.github.astrarre.transfer.v0.lba.keys.ReferenceKey;
import org.jetbrains.annotations.Nullable;

import net.minecraft.item.ItemStack;

public class ReferenceAndLimitedConsumerParticipant implements Participant<ItemKey> {
	protected final ReferenceKey<ItemStack> reference;
	@Nullable
	protected final LimitedConsumer<ItemStack> overflow;

	public ReferenceAndLimitedConsumerParticipant(Reference<ItemStack> reference, @Nullable LimitedConsumer<ItemStack> overflow) {
		this.reference = new ReferenceKey<>(reference);
		this.overflow = overflow;
	}

	@Override
	public void extract(@Nullable Transaction transaction, Insertable<ItemKey> insertable) {

	}

	@Override
	public int extract(@Nullable Transaction transaction, ItemKey type, int quantity) {
		return 0;
	}

	@Override
	public int insert(@Nullable Transaction transaction, ItemKey type, int quantity) {

		return 0;
	}


}
