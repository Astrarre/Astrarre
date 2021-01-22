package io.github.astrarre.itemview.v0.api.item;

import io.github.astrarre.itemview.internal.ItemKeyImpl;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.stripper.Hide;
import io.github.astrarre.v0.item.Item;
import io.github.astrarre.v0.item.ItemStack;

import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;

/**
 * an Item and it's NBT data
 */
public interface ItemKey {
	ItemKey EMPTY = new ItemKeyImpl(Items.AIR, NBTagView.EMPTY);

	@Hide
	static ItemKey of(net.minecraft.item.ItemStack stack) {
		if (stack.isEmpty()) {
			return EMPTY;
		} else if (stack.hasTag()) {
			return new ItemKeyImpl(stack.getItem(), stack.getTag());
		} else {
			return of(stack.getItem());
		}
	}

	// todo cache ItemKey with versioning for mutable array tags

	@Hide
	static ItemKey of(net.minecraft.item.Item item) {
		return (ItemKey) item;
	}

	default Item getItem() {
		return (Item) this.asItem();
	}

	@Hide
	net.minecraft.item.Item asItem();

	default ItemStack newItemStack(int count) {
		return (ItemStack) (Object) this.createItemStack(count);
	}

	@Hide
	default net.minecraft.item.ItemStack createItemStack(int count) {
		net.minecraft.item.ItemStack stack = new net.minecraft.item.ItemStack(this.asItem(), count);
		stack.setTag(this.getTag().copyTag());
		return stack;
	}

	/**
	 * @return an immutable view of the ItemKey
	 */
	NBTagView getTag();

	@Hide
	default CompoundTag getCompoundTag() {
		return this.getTag().copyTag();
	}

	default boolean isEqual(ItemStack stack) {
		return this.isEqual((net.minecraft.item.ItemStack) (Object) stack);
	}

	@Hide
	default boolean isEqual(net.minecraft.item.ItemStack stack) {
		return stack.getItem() == this.asItem() && this.getTag().equals(stack.getTag());
	}

	/**
	 * @return the maximum stack size of the item
	 */
	int getMaxStackSize();
}
