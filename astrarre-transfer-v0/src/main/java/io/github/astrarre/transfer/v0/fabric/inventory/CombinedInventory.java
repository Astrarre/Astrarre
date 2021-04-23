package io.github.astrarre.transfer.v0.fabric.inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * an inventory of inventories
 */
public class CombinedInventory implements Inventory {
	protected final List<Inventory> inventories;
	protected final boolean cache;
	protected int size = -1;

	/**
	 * @param cache true if the sizes of the inventories do not change
	 */
	public static Inventory combine(boolean cache, Inventory... inventories) {
		return new CombinedInventory(new ArrayList<>(new HashSet<>(Arrays.asList(inventories))), cache);
	}

	/**
	 * @param cache true if the sizes of the inventories do not change
	 */
	public CombinedInventory(List<Inventory> inventories, boolean cache) {
		this.inventories = inventories;
		this.cache = cache;
	}

	protected SlotAccess get(int slot) {
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
		if(this.size != -1 && this.cache) {
			return this.size;
		}

		int index = 0;
		for (Inventory inventory : this.inventories) {
			index += inventory.size();
		}
		this.size = index;
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

	protected static final class SlotAccess {
		public final Inventory inventory;
		public final int slot;

		private SlotAccess(Inventory inventory, int slot) {
			this.inventory = inventory;
			this.slot = slot;
		}
	}
}
