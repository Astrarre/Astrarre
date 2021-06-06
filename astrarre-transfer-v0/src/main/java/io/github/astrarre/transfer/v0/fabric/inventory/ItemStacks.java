package io.github.astrarre.transfer.v0.fabric.inventory;

import net.minecraft.item.ItemStack;

public class ItemStacks {
	/**
	 * The original stack is mutated, and becomes the 'leftover'
	 *
	 * @return the extracted stack
	 */
	public static ItemStack extract(ItemStack stack, int amount) {
		if (amount == 0 || stack.isEmpty()) {
			return ItemStack.EMPTY;
		}
		ItemStack extracted = stack.copy();
		int toExtract = Math.min(stack.getCount(), amount);
		extracted.setCount(toExtract);
		stack.decrement(toExtract);
		return extracted;
	}

	/**
	 * @return the combined stack
	 *  if insert is incompatible with the destination, it returns a copy of destination
	 *  if insert#count + destination#count > destination#maxCount, then returnstack#count == destination#maxcount
	 */
	public static ItemStack insert(ItemStack destination, ItemStack insert) {
		if (destination.isEmpty()) {
			return insert.copy();
		}
		int capacity = destination.getMaxCount() - destination.getCount();
		if (capacity > 0 && ItemStack.areItemsEqual(destination, insert) && ItemStack.areTagsEqual(destination, insert)) {
			ItemStack combined = destination.copy();
			int toInsert = Math.min(capacity, insert.getCount());
			combined.increment(toInsert);
			return combined;
		} else {
			return destination.copy();
		}
	}
}
