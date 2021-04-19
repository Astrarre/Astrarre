package io.github.astrarre.transfer.v0.lba;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.ItemInsertable;
import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.transfer.v0.api.Insertable;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import io.github.astrarre.transfer.v0.api.transaction.keys.ObjectKeyImpl;
import org.jetbrains.annotations.Nullable;

import net.minecraft.item.ItemStack;

public class ItemInsertableInsertable implements Insertable<ItemKey> {
	protected final ItemInsertable insertable;
	protected final ItemInsertableKey key;

	public ItemInsertableInsertable(ItemInsertable insertable) {
		this.insertable = insertable;
		this.key = new ItemInsertableKey();
	}

	@Override
	public int insert(@Nullable Transaction transaction, ItemKey type, int quantity) {
		ItemStack current = this.key.get(transaction);
		if(current.isEmpty() || type.isEqual(current)) {
			int combinedSize = Math.min(type.getMaxStackSize(), quantity + current.getCount());
			ItemStack success = type.createItemStack(combinedSize);
			ItemStack remainder = this.insertable.attemptInsertion(success, Simulation.SIMULATE);
			success.decrement(remainder.getCount());
			this.key.set(transaction, success);
			return success.getCount() - current.getCount();
		}
		return 0;
	}

	protected class ItemInsertableKey extends ObjectKeyImpl<ItemStack> {
		@Override
		protected ItemStack getRootValue() {
			return ItemStack.EMPTY;
		}

		@Override
		protected void setRootValue(ItemStack val) {
			ItemInsertableInsertable.this.insertable.insert(val);
		}
	}
}
