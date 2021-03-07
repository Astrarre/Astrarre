package io.github.astrarre.transfer.v0.fabric.participants.item;

import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import io.github.astrarre.transfer.v0.api.participants.FixedObjectVolume;

/**
 * A FixedObjectVolume for TaggedItem (it uses the Item's max stack size).
 * If initialized with a custom max size, it will take the min of the size of the stack and the passed size
 */
public class ItemSlotParticipant extends FixedObjectVolume<ItemKey> {
	public ItemSlotParticipant() {
		this(64);
	}

	public ItemSlotParticipant(int max) {
		this(ItemKey.EMPTY, 0, max);
	}

	public ItemSlotParticipant(ItemKey key, int quantity) {
		this(key, quantity, key.getMaxStackSize());
	}

	public ItemSlotParticipant(ItemKey object, int quantity, int max) {
		super(ItemKey.EMPTY, object, quantity, max);
	}

	@Override
	public int getMax(Transaction transaction) {
		return Math.min(this.type.get(transaction).getMaxStackSize(), super.getMax(transaction));
	}

	@Override
	public int getMaxStackSize(ItemKey type) {
		return type.getMaxStackSize();
	}
}
