package io.github.astrarre.itemview.v0.api.item;

import io.github.astrarre.itemview.internal.FabricItemViews;
import io.github.astrarre.stripper.Hide;
import io.github.astrarre.v0.item.ItemStack;


/**
 * do NOT implement this interface!
 *
 * an unmodifiable view of an ItemStack
 */
public interface ItemStackView extends ItemKey {
	ItemStackView EMPTY = FabricItemViews.create(null);

	int getCount();

	ItemStackView copy();

	/**
	 * @return true if the item, count and nbt tags are equal
	 */
	boolean equals(ItemStackView view);

	@Hide
	boolean equals(net.minecraft.item.ItemStack stack);

	default boolean equals(ItemStack stack) {
		return this.equals((net.minecraft.item.ItemStack) (Object) stack);
	}
}
