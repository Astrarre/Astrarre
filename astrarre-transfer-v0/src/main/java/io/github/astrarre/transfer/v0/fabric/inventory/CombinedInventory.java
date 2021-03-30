package io.github.astrarre.transfer.v0.fabric.inventory;

import java.util.Set;
import java.util.function.Predicate;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class CombinedInventory implements Inventory {
	private final Inventory[] inventories;

	public static Inventory combine(Inventory... inventories) {
		return new CombinedInventory(inventories);
	}

	public CombinedInventory(Inventory[] inventories) {
		this.inventories = inventories;
	}

	private SlotAccess get(int slot) {
		for (Inventory inventory : this.inventories) {
			if (slot - inventory.size() < 0) {
				return new SlotAccess(inventory, slot);
			} else {
				slot -= inventory.size();
			}
		}
		throw new IndexOutOfBoundsException();
	}

	@Override
	public int size() {
		int index = 0;
		for (Inventory inventory : this.inventories) {
			index += inventory.size();
		}
		return index;
	}

	@Override
	public boolean isEmpty() {
		return this.allMatch(Inventory::isEmpty);
	}

	public boolean allMatch(Predicate<Inventory> predicate) {
		for (Inventory inventory : this.inventories) {
			if (!predicate.test(inventory)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public ItemStack getStack(int slot) {
		SlotAccess access = this.get(slot);
		return access.inventory.getStack(access.slot);
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		SlotAccess access = this.get(slot);
		return access.inventory.removeStack(access.slot, amount);
	}

	@Override
	public ItemStack removeStack(int slot) {
		SlotAccess access = this.get(slot);
		return access.inventory.removeStack(access.slot);
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		SlotAccess access = this.get(slot);
		access.inventory.setStack(access.slot, stack);
	}

	@Override
	public int getMaxCountPerStack() {
		int min = Integer.MAX_VALUE;
		for (Inventory inventory : this.inventories) {
			min = Math.min(min, inventory.getMaxCountPerStack());
		}
		return min;
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
			if (inventory.canPlayerUse(player)) {
				return true;
			}
		}
		return false;
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
		SlotAccess access = this.get(slot);
		return access.inventory.isValid(access.slot, stack);
	}

	@Override
	public int count(Item item) {
		int sum = 0;
		for (Inventory inventory : this.inventories) {
			sum += inventory.count(item);
		}
		return sum;
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

	@Override
	public void clear() {
		for (Inventory inventory : this.inventories) {
			inventory.clear();
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		int index = 0;
		for (Inventory inventory : this.inventories) {
			for (int i = 0; i < inventory.size(); i++) {
				if (index % 9 == 0) {
					builder.append('\n');
				} else {
					builder.append('\t');
				}
				index++;
				builder.append('[').append(inventory.getStack(i)).append(']');
			}
		}
		return builder.toString();
	}

	private static final class SlotAccess {
		private final Inventory inventory;
		private final int slot;

		private SlotAccess(Inventory inventory, int slot) {
			this.inventory = inventory;
			this.slot = slot;
		}
	}
}
