package io.github.astrarre.transfer.v0.lba.item;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.ItemExtractable;
import alexiil.mc.lib.attributes.item.filter.ExactItemStackFilter;
import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.transfer.v0.api.Extractable;
import io.github.astrarre.transfer.v0.api.Insertable;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import io.github.astrarre.transfer.v0.api.transaction.keys.ObjectKeyImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.item.ItemStack;

public class ItemExtractableExtractable implements Extractable<ItemKey> {
	protected final ItemExtractable extractable;
	protected final ItemInsertableKey toExtract;

	public ItemExtractableExtractable(ItemExtractable extractable) {
		this.extractable = extractable;
		this.toExtract = new ItemInsertableKey();
	}

	@Override
	public void extract(@Nullable Transaction transaction, Insertable<ItemKey> insertable) {
		ItemStack current = this.toExtract.get(transaction);
		if(current.isEmpty()) {
			int[] validFound = {0};
			ItemStack toExtract = this.extractable.attemptExtraction(stack -> {
				try(Transaction transaction1 = Transaction.create(false)) {
					int c = insertable.insert(transaction1, ItemKey.of(stack), stack.getCount());
					if(c > 0) {
						validFound[0] = c;
						return true;
					}
					return false;
				}
			}, 1, Simulation.SIMULATE);

			if(validFound[0] == 0 || toExtract.isEmpty()) {
				return;
			}

			ItemStack verify = this.extractable.attemptExtraction(new ExactItemStackFilter(toExtract), validFound[0], Simulation.SIMULATE);
			if(!verify.isEmpty()) {
				try(Transaction transaction1 = Transaction.create()) {
					if(insertable.insert(transaction, ItemKey.of(verify), verify.getCount()) != verify.getCount()) {
						transaction1.abort();
						return;
					}
				}
				this.toExtract.set(transaction, verify);
			}
		}
	}

	@Override
	public int extract(@Nullable Transaction transaction, @NotNull ItemKey type, int quantity) {
		ItemStack toExtract = this.toExtract.get(transaction);
		if(toExtract.isEmpty() || type.isEqual(toExtract)) {
			int combinedSize = Math.min(type.getMaxStackSize(), quantity + toExtract.getCount());
			ItemStack success = type.createItemStack(combinedSize);
			ItemStack remainder = this.extractable.attemptExtraction(new ExactItemStackFilter(success), success.getCount(), Simulation.SIMULATE);
			success.decrement(remainder.getCount());
			this.toExtract.set(transaction, success);
			return success.getCount() - toExtract.getCount();
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
			ItemExtractableExtractable.this.extractable.extract(val, val.getCount());
		}
	}
}
