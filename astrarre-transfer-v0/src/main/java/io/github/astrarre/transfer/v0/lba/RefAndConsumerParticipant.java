package io.github.astrarre.transfer.v0.lba;

import alexiil.mc.lib.attributes.misc.LimitedConsumer;
import alexiil.mc.lib.attributes.misc.Reference;
import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.transfer.v0.api.Insertable;
import io.github.astrarre.transfer.v0.api.Participant;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import io.github.astrarre.transfer.v0.lba.adapters.ItemInsertableLimitedConsumer;
import io.github.astrarre.transfer.v0.lba.item.ItemInsertableInsertable;
import io.github.astrarre.transfer.v0.lba.keys.ReferenceKey;
import org.jetbrains.annotations.Nullable;

import net.minecraft.item.ItemStack;

public class RefAndConsumerParticipant implements Participant<ItemKey> {
	protected final ReferenceKey<ItemStack> reference;
	@Nullable protected final LimitedConsumer<ItemStack> overflow;
	protected final Insertable<ItemKey> overflowInsertable;
	protected final ItemStack stack;

	public RefAndConsumerParticipant(Reference<ItemStack> reference, @Nullable LimitedConsumer<ItemStack> overflow) {
		this.reference = new ReferenceKey<>(reference);
		this.overflow = overflow;
		this.stack = reference.get();
		if (overflow != null) {
			this.overflowInsertable = new ItemInsertableInsertable(ItemInsertableLimitedConsumer.from(overflow));
		} else {
			this.overflowInsertable = null;
		}
	}

	@Override
	public void extract(@Nullable Transaction transaction, Insertable<ItemKey> insertable) {
		ItemStack ref = this.reference.get(transaction);
		int inserted = insertable.insert(transaction, ItemKey.of(ref), ref.getCount());
		if (inserted != 0) {
			ref = ref.copy();
			ref.decrement(inserted);
			this.reference.set(transaction, ref);
		}
	}

	@Override
	public int extract(@Nullable Transaction transaction, ItemKey type, int quantity) {
		ItemStack ref = this.reference.get(transaction);
		if (type.isEqual(ref)) {
			int toTake = Math.min(quantity, ref.getCount());
			ref = ref.copy();
			ref.decrement(toTake);
			this.reference.set(transaction, ref);
			return toTake;
		}
		return 0;
	}

	@Override
	public int insert(@Nullable Transaction transaction, ItemKey type, int quantity) {
		if (this.stack.getItem() == type.getItem()) {
			ItemStack reference = this.reference.get(transaction);
			int toInsert = Math.min(reference.getMaxCount() - reference.getCount(), quantity);
			if (toInsert != 0) {
				reference = reference.copy();
				reference.increment(toInsert);
				this.reference.set(transaction, reference);
				return toInsert;
			}
		}

		if (this.overflowInsertable == null) {
			return 0;
		} else {
			return this.overflowInsertable.insert(transaction, type, quantity);
		}
	}
}
