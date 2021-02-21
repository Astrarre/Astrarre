package io.github.astrarre.access.internal.inventory;


import java.util.Set;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class CombinedInventory implements Inventory {
	protected final Inventory[] inventories;

	public CombinedInventory(Inventory[] inventories) {this.inventories = inventories;}

	@Override
	public void clear() {
		for (Inventory inventory : this.inventories) {
			inventory.clear();
		}
	}

	protected interface Func<T> {
		T apply(Inventory inventory, int slot);
	}

	@Override
	public int size() {
		int size = 0;
		for (Inventory inventory : this.inventories) {
			size += inventory.size();
		}
		return size;
	}


	@Override
	public boolean isEmpty() {
		for (Inventory inventory : this.inventories) {
			if (!inventory.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public ItemStack getStack(int slot) {
		return this.apply(slot, Inventory::getStack);
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		return this.apply(slot, (i, s) -> i.removeStack(s, amount));
	}

	@Override
	public ItemStack removeStack(int slot) {
		return this.apply(slot, Inventory::removeStack);
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		this.apply(slot, (i, s) -> {
			i.setStack(s, stack);
			return null;
		});
	}

	@Override
	public int getMaxCountPerStack() {
		int max = Integer.MIN_VALUE;
		for (Inventory inventory : this.inventories) {
			int val = inventory.getMaxCountPerStack();
			if (val > max) {
				max = val;
			}
		}
		return max;
	}

	@Override
	public void markDirty() {
		for (Inventory inventory : this.inventories) {
			inventory.markDirty();
		}
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		for (Inventory inventory : this.inventories) {
			if (!inventory.canPlayerUse(player)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void onOpen(PlayerEntity player) {
		for (Inventory inventory : this.inventories) {
			inventory.onOpen(player);
		}
	}

	@Override
	public void onClose(PlayerEntity player) {
		for (Inventory inventory : this.inventories) {
			inventory.onClose(player);
		}
	}

	@Override
	public boolean isValid(int slot, ItemStack stack) {
		return this.apply(slot, (i, s) -> i.isValid(s, stack));
	}

	@Override
	public int count(Item item) {
		int count = 0;
		for (Inventory inventory : this.inventories) {
			count += inventory.count(item);
		}
		return count;
	}

	@Override
	public boolean containsAny(Set<Item> items) {
		for (Inventory inventory : this.inventories) {
			if (inventory.containsAny(items)) {
				return true;
			}
		}
		return false;
	}


	protected <T> T apply(int slot, Func<T> func) {
		int or = slot;
		for (Inventory inventory : this.inventories) {
			if (slot < inventory.size()) {
				return func.apply(inventory, slot);
			} else {
				slot -= inventory.size();
			}
		}
		throw new IndexOutOfBoundsException(or + " >= " + this.size());
	}
}
