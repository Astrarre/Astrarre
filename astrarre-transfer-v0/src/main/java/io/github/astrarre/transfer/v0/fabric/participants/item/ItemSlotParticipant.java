package io.github.astrarre.transfer.v0.fabric.participants.item;

import io.github.astrarre.itemview.v0.api.Serializer;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.itemview.v0.fabric.FabricSerializers;
import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import io.github.astrarre.transfer.v0.api.participants.FixedObjectVolume;
import io.github.astrarre.transfer.v0.fabric.participants.FabricParticipants;

import net.minecraft.item.ItemStack;

/**
 * A FixedObjectVolume for ItemKey (it uses the Item's max stack size).
 * If initialized with a custom max size, it will take the min of the size of the stack and the passed size
 */
public class ItemSlotParticipant extends FixedObjectVolume<ItemKey> {
	public static final Serializer<ItemSlotParticipant> ITEM_KEY_SERIALIZER = Serializer.of((tag) -> {
		NBTagView volume = tag.asTag();
		int max = volume.getInt("max");
		ItemStack object = FabricSerializers.ITEM_STACK.read(volume, "object");
		return new ItemSlotParticipant(object, max);
	}, t -> {
		NBTagView.Builder volume = NBTagView.builder().putInt("max", t.getMax(Transaction.GLOBAL));
		FabricSerializers.ITEM_STACK.save(volume, "object", t.type.get(Transaction.GLOBAL).createItemStack(t.quantity.get(Transaction.GLOBAL)));
		return volume;
	});

	public ItemSlotParticipant() {
		this(64);
	}

	public ItemSlotParticipant(int max) {
		this(ItemKey.EMPTY, 0, max);
	}

	public ItemSlotParticipant(ItemStack stack) {
		this(ItemKey.of(stack), stack.getCount());
	}

	public ItemSlotParticipant(ItemStack stack, int max) {
		this(ItemKey.of(stack), stack.getCount(), max);
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
