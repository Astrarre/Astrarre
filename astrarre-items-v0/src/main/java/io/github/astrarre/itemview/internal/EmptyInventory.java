package io.github.astrarre.itemview.internal;

import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;

public class EmptyInventory implements SidedInventory {
	@Override
	public int size() {
		return 0;
	}

	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	public ItemStack getStack(int slot) {
		throw new IndexOutOfBoundsException("size is 0, index:" + slot);
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		throw new IndexOutOfBoundsException("size is 0, index:" + slot);
	}

	@Override
	public ItemStack removeStack(int slot) {
		throw new IndexOutOfBoundsException("size is 0, index:" + slot);
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		throw new IndexOutOfBoundsException("size is 0, index:" + slot);
	}

	@Override
	public void markDirty() {}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {return false;}

	@Override
	public void onOpen(PlayerEntity player) {}

	@Override
	public void onClose(PlayerEntity player) {}

	@Override
	public boolean isValid(int slot, ItemStack stack) {
		throw new IndexOutOfBoundsException("size is 0, index:" + slot);
	}

	@Override
	public int count(Item item) {return 0;}

	@Override
	public boolean containsAny(Set<Item> items) {
		return false;
	}

	@Override
	public void clear() {}

	@Override
	public int[] getAvailableSlots(Direction side) {
		return ArrayUtils.EMPTY_INT_ARRAY;
	}

	@Override
	public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
		throw new IndexOutOfBoundsException("size is 0, index:" + slot);
	}

	@Override
	public boolean canExtract(int slot, ItemStack stack, Direction dir) {
		throw new IndexOutOfBoundsException("size is 0, index:" + slot);
	}
}
