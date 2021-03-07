package io.github.astrarre.transfer.internal.access;

import java.util.function.Consumer;

import net.minecraft.item.ItemStack;

public interface ItemStackAccess {
	static ItemStackAccess of(ItemStack stack) {
		return (ItemStackAccess) (Object) stack;
	}

	/**
	 * called when the stack size of the item changes
	 */
	void astrarre_onChange(Consumer<ItemStack> stack);

}
