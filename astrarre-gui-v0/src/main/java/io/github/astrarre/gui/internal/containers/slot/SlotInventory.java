package io.github.astrarre.gui.internal.containers.slot;

import java.util.Set;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SlotInventory implements Inventory {
	public ItemStack stack = ItemStack.EMPTY;

	@Override
	public int size() {
		return 1;
	}

	@Override
	public boolean isEmpty() {
		return this.stack == ItemStack.EMPTY;
	}

	@Override
	public ItemStack getStack(int slot) {
		return this.stack;
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		ItemStack stack = this.stack;
		if (amount < stack.getCount()) {
			stack.setCount(stack.getCount() - amount);
			ItemStack clone = stack.copy();
			clone.setCount(amount);
			return clone;
		} else {
			this.stack = ItemStack.EMPTY;
			return stack;
		}
	}

	@Override
	public ItemStack removeStack(int slot) {
		return this.removeStack(slot, Integer.MAX_VALUE);
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		if (slot != 0) {
			throw new IndexOutOfBoundsException(slot + " != 0");
		}
		this.stack = stack;
	}

	@Override
	public int getMaxCountPerStack() {
		return Math.max(this.stack.getMaxCount(), 64);
	}

	@Override
	public void markDirty() {

	}

	@Override
	public int count(Item item) {
		if(this.stack.getItem() == item) {
			return this.stack.getCount();
		}
		return 0;
	}

	@Override
	public void clear() {
		this.stack = ItemStack.EMPTY;
	}

	@Override
	public boolean containsAny(Set<Item> items) {
		return items.contains(this.stack.getItem());
	}

	@Override public boolean canPlayerUse(PlayerEntity player) {return true;}
	@Override public void onOpen(PlayerEntity player) {}
	@Override public void onClose(PlayerEntity player) {}
	@Override public boolean isValid(int slot, ItemStack stack) {return false;}
}
