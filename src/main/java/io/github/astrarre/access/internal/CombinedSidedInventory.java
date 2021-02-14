package io.github.astrarre.access.internal;

import org.jetbrains.annotations.Nullable;

import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;

public class CombinedSidedInventory extends CombinedInventory implements SidedInventory {
	public CombinedSidedInventory(Inventory[] inventories) {
		super(inventories);
	}

	@Override
	public int[] getAvailableSlots(Direction side) {
		int slot = 0;
		for (int i = 0; i < side.ordinal(); i++) {
			Inventory inventory = this.inventories[i];
			slot += inventory.size();
		}

		Inventory at = this.inventories[side.ordinal()];
		// todo optimize
		int[] array = new int[at.size()];
		for (int i = 0; i < array.length; i++) {
			array[i] = i + slot;
		}
		return array;
	}

	@Override
	public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
		return this.apply(slot, (i, s) -> {
			if(dir == null || i == this.inventories[dir.ordinal()]) {
				return i.isValid(slot, stack) && i instanceof FilteringInventory && ((FilteringInventory) i).canInsert(slot, stack);
			}
			return false;
		});
	}

	@Override
	public boolean canExtract(int slot, ItemStack stack, Direction dir) {
		return this.apply(slot, (i, s) -> {
			if(dir == null || i == this.inventories[dir.ordinal()]) {
				return i instanceof FilteringInventory && ((FilteringInventory) i).canInsert(slot, stack);
			}
			return false;
		});
	}
}
