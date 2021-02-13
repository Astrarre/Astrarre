package io.github.astrarre.transfer.internal;

import io.github.astrarre.itemview.v0.api.item.ItemKey;
import io.github.astrarre.transfer.v0.api.transaction.keys.generated.IntKeyImpl;
import io.github.astrarre.transfer.v0.api.Insertable;
import io.github.astrarre.transfer.v0.api.Participant;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import io.github.astrarre.transfer.v0.api.transaction.keys.ObjectKeyImpl;

import net.minecraft.inventory.Inventory;

public class SlotParticipant implements Participant<ItemKey> {
	private final Inventory inventory;
	private final int index;
	private final SlotItemKey item = new SlotItemKey();
	private final SlotCountKey count = new SlotCountKey();

	public SlotParticipant(Inventory inventory, int index) {
		this.inventory = inventory;
		this.index = index;
	}

	@Override
	public void extract(Transaction transaction, Insertable<ItemKey> insertable) {
		this.count.set(transaction, insertable.insert(transaction, this.item.get(transaction), this.count.get(transaction)));
	}

	@Override
	public int extract(Transaction transaction, ItemKey type, int amount) {
		if (type != ItemKey.EMPTY && amount != 0 && this.item.get(transaction).equals(type)) {
			int count = this.count.get(transaction);
			int toTake = Math.min(count, amount);
			if (toTake == count) {
				this.item.set(transaction, ItemKey.EMPTY);
			}

			this.count.decrement(transaction, toTake);
			return toTake;
		}
		return 0;
	}

	@Override
	public int insert(Transaction transaction, ItemKey type, int amount) {
		if(type == ItemKey.EMPTY || amount == 0) return 0;

		ItemKey itemType = this.item.get(transaction);
		int currentCount = this.count.get(transaction);

		if (itemType.getMaxStackSize() > currentCount && itemType == ItemKey.EMPTY || itemType.equals(type)) {
			if (itemType == ItemKey.EMPTY) {
				// set new type
				this.item.set(transaction, type);
			}

			int toInsert = Math.min(itemType.getMaxStackSize() - currentCount, amount);
			this.count.increment(transaction, toInsert);
			return toInsert;
		}
		return 0;
	}

	// item key must be called before count key!
	public class SlotItemKey extends ObjectKeyImpl<ItemKey> {
		@Override
		protected ItemKey getRootValue() {
			return ItemKey.of(SlotParticipant.this.inventory.getStack(SlotParticipant.this.index));
		}

		@Override
		protected void setRootValue(ItemKey val) {
			SlotParticipant.this.inventory.setStack(SlotParticipant.this.index, val.createItemStack(1));
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
