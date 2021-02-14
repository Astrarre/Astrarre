package io.github.astrarre.access.internal;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public interface FilteringInventory extends Inventory {
	boolean canInsert(int slot, ItemStack stack);

	boolean canExtract(int slot, ItemStack stack);
}
