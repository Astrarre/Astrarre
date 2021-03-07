package io.github.astrarre.itemview.v0.fabric;

import io.github.astrarre.itemview.internal.TaggedItemImpl;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;

/**
 * an Item and it's NBT data (guaranteed immutable)
 */
public interface ItemKey {
	ItemKey EMPTY = new TaggedItemImpl(Items.AIR, NBTagView.EMPTY);

	// todo cache ItemKey with versioning for mutable array tags

	default ItemStack createItemStack(int count) {
		ItemStack stack = new ItemStack(this.getItem(), count);
		stack.setTag(this.getTag().copyTag());
		return stack;
	}

	Item getItem();

	/**
	 * @return an immutable view of the ItemKey
	 */
	NBTagView getTag();

	default CompoundTag getCompoundTag() {
		return this.getTag().copyTag();
	}

	default boolean isEqual(ItemStack stack) {
		return stack.getItem() == this.getItem() && this.getTag().equals(stack.getTag());
	}

	/**
	 * @return the maximum stack size of the item
	 */
	int getMaxStackSize();

	default ItemKey withTag(NBTagView n) {
		if (n.isEmpty()) {
			return (ItemKey) this.getItem();
		}

		if(this.getItem() == Items.AIR) {
			return (ItemKey) Items.AIR;
		}

		return new TaggedItemImpl(this.getItem(), n);
	}

	static ItemKey of(ItemStack stack) {
		if (stack.isEmpty()) {
			return EMPTY;
		} else if (stack.hasTag()) {
			return new TaggedItemImpl(stack.getItem(), stack.getTag());
		} else {
			return of(stack.getItem());
		}
	}

	static ItemKey of(Item item) {
		return (ItemKey) item;
	}
}
