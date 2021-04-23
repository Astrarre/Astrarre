package io.github.astrarre.transfer.v0.fabric.inventory;

import java.util.Set;

import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;

/**
 * an inventory that voids everything inserted into it
 */
public class VoidingInventory implements SidedInventory {
	private static final int SIZE = 128;
	private static final int[] ARRAY = new int[SIZE];
	static {
		for (int i = 0; i < ARRAY.length; i++) {
			ARRAY[i] = i;
		}
	}
	public static final VoidingInventory INSTANCE = new VoidingInventory();
	protected VoidingInventory() {
	}

	@Override
	public int size() {
		return SIZE;
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
		return true;
	}

	@Override
	public void clear() {
	}

	@Override
	public int[] getAvailableSlots(Direction side) {
		return ARRAY;
	}

	@Override
	public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
		return true;
	}

	@Override
	public boolean canExtract(int slot, ItemStack stack, Direction dir) {
		return false;
	}

	@Override
	public int getMaxCountPerStack() {
		return 128;
	}

	@Override
	public void onOpen(PlayerEntity player) {
	}

	@Override
	public void onClose(PlayerEntity player) {
	}

	@Override
	public boolean isValid(int slot, ItemStack stack) {
		return true;
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
