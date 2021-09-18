package io.github.astrarre.transfer.v0.fabric.inventory;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

/**
 * a list implementation for the Inventory class
 */
public class InventoryList extends DefaultedList<ItemStack> {
	public final Inventory inventory;

	public InventoryList(Inventory inventory) {
		super(List.of(), ItemStack.EMPTY);
		this.inventory = inventory;
	}

	@Override
	public @NotNull ItemStack get(int index) {
		return this.inventory.getStack(index);
	}

	@Override
	public ItemStack set(int index, ItemStack element) {
		ItemStack old = this.inventory.getStack(index);
		this.inventory.setStack(index, element);
		return old;
	}

	@Override
	public ItemStack remove(int index) {
		return this.inventory.removeStack(index);
	}

	@Override
	public int size() {
		return this.inventory.size();
	}
}
