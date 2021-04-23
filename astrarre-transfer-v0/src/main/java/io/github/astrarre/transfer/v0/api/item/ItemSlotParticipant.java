package io.github.astrarre.transfer.v0.api.item;

import io.github.astrarre.itemview.v0.api.Serializer;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.itemview.v0.fabric.FabricSerializers;
import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.transfer.v0.api.Insertable;
import io.github.astrarre.transfer.v0.api.Participant;
import io.github.astrarre.transfer.v0.api.participants.array.Slot;
import io.github.astrarre.transfer.v0.api.transaction.Key;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import io.github.astrarre.transfer.v0.api.transaction.keys.ObjectKeyImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

/**
 * A FixedObjectVolume for ItemKey (it uses the Item's max stack size). If initialized with a custom max size, it will take the min of the size of the
 * stack and the passed size
 */
public class ItemSlotParticipant implements Slot<ItemKey> {
	public static final Serializer<ItemSlotParticipant> ITEM_KEY_SERIALIZER = Serializer.of((tag) -> new ItemSlotParticipant(FabricSerializers.ITEM_STACK.read(tag)), t -> FabricSerializers.ITEM_STACK.save(t.type.get(Transaction.GLOBAL)));

	public final Key.Object<ItemStack> type;
	public int max = 64;

	public ItemSlotParticipant() {
		this(ItemKey.EMPTY, 0);
	}

	public ItemSlotParticipant(ItemKey object, int quantity) {
		if (object == ItemKey.EMPTY && quantity != 0) {
			throw new IllegalArgumentException("cannot have " + quantity + " units of EMPTY!");
		} else if (quantity < 0) {
			throw new IllegalArgumentException("Cannot have negative units of " + object);
		}

		if (quantity == 0) {
			object = ItemKey.EMPTY;
		}

		this.type = new ObjectKeyImpl<>(object.createItemStack(quantity));
	}

	protected ItemSlotParticipant(Key.Object<ItemStack> key) {
		this.type = key;
	}

	public ItemSlotParticipant(ItemStack stack) {
		this.type = new ObjectKeyImpl<>(stack);
	}

	public ItemStack getStack(@Nullable Transaction transaction) {
		return this.type.get(transaction);
	}

	@Override
	public int insert(@Nullable Transaction transaction, @NotNull ItemKey type, int quantity) {
		if (quantity == 0) {
			return 0;
		}

		ItemStack stack = this.type.get(transaction);
		if (stack.isEmpty()) {
			this.type.set(transaction, type.createItemStack(quantity));
			return quantity;
		} else if (type.isEqual(stack)) {
			quantity = Math.min(Math.min(this.getMax(type), this.max) - stack.getCount(), quantity);
			ItemStack copy = stack.copy();
			copy.setCount(stack.getCount() + quantity);
			this.type.set(transaction, copy);
			return quantity;
		}
		return 0;
	}

	@Override
	public void extract(@Nullable Transaction transaction, Insertable<ItemKey> insertable) {
		ItemStack stack = this.type.get(transaction);
		if(stack.isEmpty()) return;
		int oldLevel = stack.getCount();
		int amount = insertable.insert(transaction, ItemKey.of(stack), oldLevel);
		int newLevel = oldLevel - amount;

		ItemStack copy = stack.copy();
		copy.setCount(newLevel);
		this.type.set(transaction, copy);
	}

	@Override
	public int extract(@Nullable Transaction transaction, @NotNull ItemKey type, int quantity) {
		if (quantity == 0) {
			return 0;
		}

		ItemStack stack = this.type.get(transaction);
		if (type.isEqual(stack)) {
			int oldLevel = stack.getCount();
			int toExtract = Math.min(oldLevel, quantity);
			int newLevel = oldLevel - toExtract;
			ItemStack copy = stack.copy();
			copy.setCount(newLevel);
			this.type.set(transaction, copy);
			return toExtract;
		}

		return 0;
	}

	@Override
	public ItemKey getKey(@Nullable Transaction transaction) {
		return ItemKey.of(this.getStack(transaction));
	}

	@Override
	public int getQuantity(@Nullable Transaction transaction) {
		return this.getStack(transaction).getCount();
	}

	@Override
	public boolean set(@Nullable Transaction transaction, ItemKey key, int quantity) {
		if(quantity <= key.getMaxStackSize()) {
			this.type.set(transaction, key.createItemStack(quantity));
			return true;
		}
		return false;
	}

	@Override
	public boolean isEmpty(@Nullable Transaction transaction) {
		return this.type.get(transaction).isEmpty();
	}

	@Override
	public void clear(@Nullable Transaction transaction) {
		this.type.set(transaction, ItemStack.EMPTY);
	}

	public int getMax(ItemKey key) {
		return key.getMaxStackSize();
	}

	@Override
	public String toString() {
		ItemStack stack = this.type.get(null);
		StringBuilder builder = new StringBuilder("[");
		builder.append(stack.getItem().toString());
		CompoundTag tag = stack.getTag();
		if(tag != null && !tag.isEmpty()) {
			builder.append(' ').append(tag);
		}
		builder.append('x').append(stack.getCount()).append(']');
		return builder.toString();
	}
}
