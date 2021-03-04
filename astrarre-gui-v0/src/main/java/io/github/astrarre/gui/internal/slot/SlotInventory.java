package io.github.astrarre.gui.internal.slot;

import java.util.Set;

import io.github.astrarre.gui.v0.fabric.adapter.Slot;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SlotInventory implements Inventory {
	public final Slot slot;

	public SlotInventory(Slot stack) {this.slot = stack;}


	@Override
	public int size() {
		return 1;
	}

	@Override
	public boolean isEmpty() {
		return this.slot.getStack() == ItemStack.EMPTY;
	}

	@Override
	public ItemStack getStack(int slot) {
		return this.slot.getStack();
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		ItemStack stack = this.slot.getStack();
		if (amount < stack.getCount()) {
			stack.setCount(stack.getCount() - amount);
			ItemStack clone = stack.copy();
			clone.setCount(amount);
			return clone;
		} else {
			this.slot.setStack(ItemStack.EMPTY);
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
		this.slot.setStack(stack);
	}

	@Override
	public int getMaxCountPerStack() {
		return Math.max(this.slot.getStack().getMaxCount(), 64);
	}

	@Override
	public void markDirty() {

	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {return true;}

	@Override
	public void onOpen(PlayerEntity player) {}

	@Override
	public void onClose(PlayerEntity player) {}

	@Override
	public boolean isValid(int slot, ItemStack stack) {
		return this.slot.isValid(stack);
	}

	@Override
	public int count(Item item) {
		if (this.slot.getStack().getItem() == item) {
			return this.slot.getStack().getCount();
		}
		return 0;
	}

	@Override
	public boolean containsAny(Set<Item> items) {
		return items.contains(this.slot.getStack().getItem());
	}

	@Override
	public void clear() {
		this.slot.setStack(ItemStack.EMPTY);
	}
}
