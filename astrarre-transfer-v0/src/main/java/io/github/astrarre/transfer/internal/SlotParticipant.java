package io.github.astrarre.transfer.internal;

import io.github.astrarre.itemview.internal.FabricViews;
import io.github.astrarre.itemview.v0.api.item.ItemKey;
import io.github.astrarre.transfer.v0.api.Insertable;
import io.github.astrarre.transfer.v0.api.Participant;
import io.github.astrarre.transfer.v0.api.transaction.Key;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import io.github.astrarre.transfer.v0.api.transaction.TransactionHandler;
import io.github.astrarre.transfer.v0.api.transaction.keys.ObjectKeyImpl;

import net.minecraft.inventory.Inventory;

public class SlotParticipant implements Participant<ItemKey> {
	private final Inventory inventory;
	private final int index;

	public SlotParticipant(Inventory inventory, int index) {
		this.inventory = inventory;
		this.index = index;
	}

	@Override
	public void extract(Transaction transaction, Insertable<ItemKey> insertable) {

	}

	@Override
	public int extract(Transaction transaction, ItemKey type, int amount) {
		return 0;
	}

	@Override
	public int insert(Transaction transaction, ItemKey type, int amount) {
		return 0;
	}

	public class SlotKey extends ObjectKeyImpl<ItemKey> {
		public SlotKey(TransactionHandler handler) {
			super(handler);
		}

		@Override
		protected ItemKey getTrue() {
			return FabricViews.view(SlotParticipant.this.inventory.getStack(SlotParticipant.this.index));
		}

		@Override
		protected void setTrue(ItemKey val) {
			super.setTrue(val);
		}
	}

}
