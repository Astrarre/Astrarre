package io.github.astrarre.transfer.internal.fabric;

import io.github.astrarre.itemview.v0.fabric.TaggedItem;
import io.github.astrarre.transfer.internal.fabric.inventory.FilteringInventory;
import io.github.astrarre.transfer.v0.api.Insertable;
import io.github.astrarre.transfer.v0.api.Participant;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import io.github.astrarre.transfer.v0.api.transaction.keys.ObjectKeyImpl;
import io.github.astrarre.transfer.v0.api.transaction.keys.generated.IntKeyImpl;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class SlotParticipant implements Participant<TaggedItem> {
	private final Inventory inventory;
	private final int index;
	private final SlotItemKey item = new SlotItemKey();
	private final SlotCountKey count = new SlotCountKey();

	public SlotParticipant(Inventory inventory, int index) {
		this.inventory = inventory;
		this.index = index;
	}

	@Override
	public void extract(Transaction transaction, Insertable<TaggedItem> insertable) {
		TaggedItem item = this.item.get(transaction);
		int count = this.count.get(transaction);
		if (this.inventory instanceof FilteringInventory && !((FilteringInventory) this.inventory)
				                                                     .canExtract(this.index, item.createItemStack(count))) {
			return;
		}

		this.count.set(transaction, insertable.insert(transaction, item, count));
	}

	@Override
	public int extract(Transaction transaction, TaggedItem type, int quantity) {
		if (type != TaggedItem.EMPTY && quantity != 0 && this.item.get(transaction).equals(type)) {
			if(this.inventory instanceof FilteringInventory && !((FilteringInventory) this.inventory)
					                                                 .canExtract(this.index, type.createItemStack(quantity))) {
				return 0;
			}

			int count = this.count.get(transaction);
			int toTake = Math.min(count, quantity);
			if (toTake == count) {
				this.item.set(transaction, TaggedItem.EMPTY);
			}

			this.count.decrement(transaction, toTake);
			return toTake;
		}
		return 0;
	}

	@Override
	public int insert(Transaction transaction, TaggedItem type, int quantity) {
		if (type == TaggedItem.EMPTY || quantity == 0) {
			return 0;
		}

		TaggedItem itemType = this.item.get(transaction);
		int currentCount = this.count.get(transaction);

		if (itemType.getMaxStackSize() > currentCount && itemType == TaggedItem.EMPTY || itemType.equals(type)) {
			int toInsert = Math.min(itemType.getMaxStackSize() - currentCount, quantity);
			ItemStack stack = itemType.createItemStack(toInsert);
			if (this.inventory.isValid(this.index, stack) || (this.inventory instanceof FilteringInventory && ((FilteringInventory) this.inventory)
					                                                                                                  .canInsert(
							                                                                                                  this.index,
							                                                                                                  stack))) {
				if (itemType == TaggedItem.EMPTY) {
					// set new type
					this.item.set(transaction, type);
				}
				this.count.increment(transaction, toInsert);
				return toInsert;
			}
		}
		return 0;
	}

	// item key must be called before count key!
	public class SlotItemKey extends ObjectKeyImpl<TaggedItem> {
		@Override
		protected TaggedItem getRootValue() {
			return TaggedItem.of(SlotParticipant.this.inventory.getStack(SlotParticipant.this.index));
		}

		@Override
		protected void setRootValue(TaggedItem val) {
			SlotParticipant.this.inventory.setStack(SlotParticipant.this.index, val.createItemStack(0));
		}
	}

	public class SlotCountKey extends IntKeyImpl {
		@Override
		protected int getRootValue() {
			return SlotParticipant.this.inventory.getStack(SlotParticipant.this.index).getCount();
		}

		@Override
		protected void setRootValue(int val) {
			SlotParticipant.this.inventory.getStack(SlotParticipant.this.index).setCount(val);
		}
	}
}
