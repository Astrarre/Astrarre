package io.github.astrarre.gui.internal;

import java.util.Set;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class NullInventory implements Inventory {
	public static final NullInventory INVENTORY = new NullInventory();

	private NullInventory() {}

	@Override
	public int size() {
		return 1;
	}

	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	public ItemStack getStack(int slot) {
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeStack(int slot) {
		return ItemStack.EMPTY;
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
	}

	@Override
	public void markDirty() {
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return false;
	}

	@Override
	public void clear() {
	}

	@Override
	public int getMaxCountPerStack() {
		return 64;
	}

	@Override
	public void onOpen(PlayerEntity player) {
	}

	@Override
	public void onClose(PlayerEntity player) {
	}

	@Override
	public boolean isValid(int slot, ItemStack stack) {
		return false;
	}

	@Override
	public int count(Item item) {
		return 0;
	}

	@Override
	public boolean containsAny(Set<Item> items) {
		return false;
	}
}
