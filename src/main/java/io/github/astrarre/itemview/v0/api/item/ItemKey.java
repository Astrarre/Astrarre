package io.github.astrarre.itemview.v0.api.item;

import io.github.astrarre.itemview.internal.TaggedItemImpl;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.itemview.v0.fabric.TaggedItem;
import io.github.astrarre.v0.item.Item;
import io.github.astrarre.v0.item.ItemStack;

import net.minecraft.item.Items;

/**
 * an item and it's nbtag. This is immutable.
 * You can make custom implementations if you so desire
 */
public interface ItemKey extends TaggedItem {
	ItemKey EMPTY = (ItemKey) new TaggedItemImpl(Items.AIR, NBTagView.EMPTY);

	/**
	 * @return return the item of the key
	 */
	Item asItem();

	/**
	 * @return a new itemstack with a copy of the nbt tag and item
	 */
	default ItemStack create(int count) {
		return (ItemStack) (Object) this.createItemStack(count);
	}

	@Override
	default ItemKey withTag(NBTagView n) {
		return (ItemKey) TaggedItem.super.withTag(n);
	}
}
