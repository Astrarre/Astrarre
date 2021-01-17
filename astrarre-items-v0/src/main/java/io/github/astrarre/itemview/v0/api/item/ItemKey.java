package io.github.astrarre.itemview.v0.api.item;

import io.github.astrarre.itemview.internal.ItemKeyImpl;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.stripper.Hide;
import io.github.astrarre.v0.item.Item;
import io.github.astrarre.v0.item.ItemStack;

import net.minecraft.nbt.CompoundTag;

/**
 * an Item and it's NBT data
 */
public interface ItemKey {
	@Hide
	static ItemKey of(net.minecraft.item.Item item) {
		return (ItemKey) item;
	}

	// todo cache ItemKey with versioning for mutable array tags

	@Hide
	static ItemKey of(net.minecraft.item.ItemStack stack) {
		if(stack.hasTag()) {
			return new ItemKeyImpl(stack.getItem(), stack.getTag());
		} else {
			return of(stack.getItem());
		}
	}

	default Item getItem() {
		return (Item) this.asItem();
	}

	/**
	 * @return an immutable view of the ItemKey
	 */
	NBTagView getTag();

	@Hide
	net.minecraft.item.Item asItem();

	@Hide
	default net.minecraft.item.ItemStack createItemStack(int count) {
		net.minecraft.item.ItemStack stack = new net.minecraft.item.ItemStack(this.asItem(), count);
		stack.setTag(this.getTag().copyTag());
		return stack;
	}

	default ItemStack newItemStack(int count) {
		return (ItemStack) (Object) this.createItemStack(count);
	}

	@Hide
	default CompoundTag getCompoundTag() {
		return this.getTag().copyTag();
	}
}
