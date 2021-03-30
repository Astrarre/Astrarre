package io.github.astrarre.transfer.v0.fabric.inventory;

import io.github.astrarre.transfer.v0.fabric.inventory.FilteringInventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;

public class SidedInventoryAccess implements Inventory, FilteringInventory {
	private int[] validSlots;
	private final SidedInventory inventory;
	private final Direction direction;

	public SidedInventoryAccess(SidedInventory inventory, Direction direction) {
		this.inventory = inventory;
		this.direction = direction;
		this.validSlots = inventory.getAvailableSlots(direction);
	}

	public void updateSlots() {
		this.validSlots = this.inventory.getAvailableSlots(this.direction);
	}

	@Override
	public int size() {
		return this.validSlots.length;
	}

	@Override
	public boolean isEmpty() {
		if(this.validSlots.length == this.inventory.size()) {
			return this.inventory.isEmpty();
		}

		for (int slot : this.validSlots) {
			if(!this.getStack(slot).isEmpty()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public ItemStack getStack(int slot) {
		return this.inventory.getStack(this.validSlots[slot]);
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		ItemStack stack = this.inventory.removeStack(this.validSlots[slot], amount);
		this.updateSlots();
		return stack;
	}

	@Override
	public ItemStack removeStack(int slot) {
		ItemStack stack = this.inventory.removeStack(this.validSlots[slot]);
		this.updateSlots();
		return stack;
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		this.inventory.setStack(this.validSlots[slot], stack);
		this.updateSlots();
	}

	@Override
	public int getMaxCountPerStack() {
		return this.inventory.getMaxCountPerStack();
	}

	@Override
	public void markDirty() {
		this.inventory.markDirty();
		this.updateSlots();
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return this.inventory.canPlayerUse(player);
	}

	@Override
	public void onOpen(PlayerEntity player) {
		this.inventory.onOpen(player);
	}

	@Override
	public void onClose(PlayerEntity player) {
		this.inventory.onClose(player);
	}

	@Override
	public boolean isValid(int slot, ItemStack stack) {
		return this.inventory.canInsert(this.validSlots[slot], stack, this.direction) && this.inventory.isValid(this.validSlots[slot], stack);
	}

	@Override
	public void clear() {
		this.inventory.clear();
		this.updateSlots();
	}

	@Override
	public boolean canInsert(int slot, ItemStack stack) {
		return this.isValid(slot, stack);
	}

	@Override
	public boolean canExtract(int slot, ItemStack stack) {
		return this.inventory.canExtract(this.validSlots[slot], stack, this.direction);
	}
}
