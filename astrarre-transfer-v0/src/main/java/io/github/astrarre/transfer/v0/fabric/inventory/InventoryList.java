package io.github.astrarre.transfer.v0.fabric.inventory;

import java.util.AbstractList;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

/**
 * a list implementation for the Inventory class
 */
public class InventoryList extends AbstractList<ItemStack> {
	public final Inventory inventory;

	public InventoryList(Inventory inventory) {
		this.inventory = inventory;
	}

	@Override
	public ItemStack set(int index, ItemStack element) {
		ItemStack old = this.inventory.getStack(index);
		this.inventory.setStack(index, element);
		return old;
	}

	@Override
	public ItemStack get(int index) {
		return this.inventory.getStack(index);
	}

	@Override
	public int size() {
		return this.inventory.size();
	}
}
