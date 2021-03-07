package io.github.astrarre.transfer.internal.inventory;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public interface FilteringInventory extends Inventory {
	boolean canInsert(int slot, ItemStack stack);

	boolean canExtract(int slot, ItemStack stack);
}
