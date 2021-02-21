package io.github.astrarre.itemview.v0.fabric;

import io.github.astrarre.itemview.internal.ItemKeyImpl;
import io.github.astrarre.itemview.v0.api.item.nbt.NBTagView;
import io.github.astrarre.stripper.Hide;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;

/**
 * an Item and it's NBT data (guaranteed immutable)
 */
public interface TaggedItem {
	TaggedItem EMPTY = new ItemKeyImpl(Items.AIR, NBTagView.EMPTY);

	// todo cache ItemKey with versioning for mutable array tags

	@Hide
	default ItemStack createItemStack(int count) {
		ItemStack stack = new ItemStack(this.getItem(), count);
		stack.setTag(this.getTag().copyTag());
		return stack;
	}

	@Hide
	Item getItem();

	/**
	 * @return an immutable view of the ItemKey
	 */
	NBTagView getTag();

	@Hide
	default CompoundTag getCompoundTag() {
		return this.getTag().copyTag();
	}

	@Hide
	default boolean isEqual(ItemStack stack) {
		return stack.getItem() == this.getItem() && this.getTag().equals(stack.getTag());
	}

	/**
	 * @return the maximum stack size of the item
	 */
	int getMaxStackSize();

	default TaggedItem withTag(NBTagView n) {
		if (n.isEmpty()) {
			return (TaggedItem) this.getItem();
		}

		if(this.getItem() == Items.AIR) {
			return (TaggedItem) Items.AIR;
		}

		return new ItemKeyImpl(this.getItem(), n);
	}

	@Hide
	static TaggedItem of(ItemStack stack) {
		if (stack.isEmpty()) {
			return EMPTY;
		} else if (stack.hasTag()) {
			return new ItemKeyImpl(stack.getItem(), stack.getTag());
		} else {
			return of(stack.getItem());
		}
	}

	@Hide
	static TaggedItem of(Item item) {
		return (TaggedItem) item;
	}
}
