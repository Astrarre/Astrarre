package io.github.astrarre.transfer.v0.fabric.inventory;

import org.jetbrains.annotations.Nullable;

import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;

/**
 * {@link ForwardingInventory} but for SidedInventory
 */
public interface SidedForwardingInventory extends ForwardingInventory, SidedInventory {
	@Override
	SidedInventory getInventoryDelegate();

	@Override
	default int[] getAvailableSlots(Direction side) {
		return this.getInventoryDelegate().getAvailableSlots(side);
	}

	@Override
	default boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
		return this.getInventoryDelegate().canInsert(slot, stack, dir);
	}

	@Override
	default boolean canExtract(int slot, ItemStack stack, Direction dir) {
		return this.getInventoryDelegate().canExtract(slot, stack, dir);
	}
}
