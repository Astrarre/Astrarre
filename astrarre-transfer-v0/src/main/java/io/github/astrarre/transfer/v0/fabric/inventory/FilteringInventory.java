package io.github.astrarre.transfer.v0.fabric.inventory;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

/**
 * used for some transfer api specific things
 */
public interface FilteringInventory extends Inventory {
	boolean canInsert(int slot, ItemStack stack);

	boolean canExtract(int slot, ItemStack stack);
}
