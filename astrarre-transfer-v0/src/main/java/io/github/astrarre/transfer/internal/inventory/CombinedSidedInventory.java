package io.github.astrarre.transfer.internal.inventory;

import java.util.Arrays;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;

import net.minecraft.Bootstrap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;

public final class CombinedSidedInventory implements SidedInventory {
	private static final int[] EMPTY = {};
	private final Inventory[] inventories;
	private final int[][] availableSlotCache = new int[6][];

	public CombinedSidedInventory(Inventory bottom, Inventory top, Inventory north, Inventory south, Inventory west, Inventory east) {
		this.inventories = new Inventory[] {
				bottom,
				top,
				north,
				south,
				west,
				east
		};
	}

	@Override
	public int[] getAvailableSlots(Direction side) {
		int ordinal = side.ordinal();
		int index = 0; // what *should* be the first index
		for (int i = 0; i < ordinal; i++) {
			index += this.inventories[i].size();
		}

		int[] curr = this.availableSlotCache[ordinal];
		Inventory targetInventory = this.inventories[ordinal];
		int targetSize = targetInventory.size();
		if (targetSize == 0) {
			return EMPTY;
		}

		// if uncached, or the length of the inventories have changed, or the first index of the array is incorrect, recompute
		boolean recompute = false;
		int[] buffer = curr;
		if (curr == null || curr.length != targetSize) {
			buffer = new int[targetSize];
			// replace old buffer
			this.availableSlotCache[ordinal] = buffer;
			recompute = true;
		} else if (curr[0] == index) {
			recompute = true;
		}

		if (recompute) {
			for (int i = 0; i < buffer.length; i++) {
				buffer[i] = index + i;
			}
		}

		return buffer;
	}

	@Override
	public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
		SlotAccess access = this.get(slot);
		if (access.source != dir) {
			return false;
		}

		if (access.inventory.isValid(access.slot, stack)) {
			return true;
		}

		if (access.inventory instanceof SidedInventory) {
			return ((SidedInventory) access.inventory).canInsert(access.slot, stack, dir);
		}

		if (access.inventory instanceof FilteringInventory) {
			return ((FilteringInventory) access.inventory).canInsert(access.slot, stack);
		}

		return false;
	}

	@Override
	public boolean canExtract(int slot, ItemStack stack, Direction dir) {
		SlotAccess access = this.get(slot);
		if (access.source != dir) {
			return false;
		}


		if (access.inventory instanceof SidedInventory) {
			return ((SidedInventory) access.inventory).canExtract(access.slot, stack, dir);
		}

		if (access.inventory instanceof FilteringInventory) {
			return ((FilteringInventory) access.inventory).canExtract(access.slot, stack);
		}

		return false;
	}

	private SlotAccess get(int slot) {
		for (int i = 0; i < this.inventories.length; i++) {
			Inventory inventory = this.inventories[i];
			if (slot - inventory.size() < 0) {
				return new SlotAccess(inventory, Direction.byId(i), slot);
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
		private final Direction source;
		private final int slot;

		private SlotAccess(Inventory inventory, Direction source, int slot) {
			this.inventory = inventory;
			this.source = source;
			this.slot = slot;
		}
	}

	/**
	 * tests sided inventory
	 */
	public static void main(String[] args) {
		Bootstrap.initialize();

		SimpleInventory first = new SimpleInventory(3) {
			@Override
			public boolean isValid(int slot, ItemStack stack) {
				return false;
			}
		}, second = new SimpleInventory(10);

		SidedInventory combined = new CombinedSidedInventory(first,
				EmptyInventory.INSTANCE,
				second,
				EmptyInventory.INSTANCE,
				EmptyInventory.INSTANCE,
				EmptyInventory.INSTANCE);

		Random random = new Random();
		for (Direction value : Direction.values()) {
			int[] slots = combined.getAvailableSlots(value);
			for (int slot : slots) {
				ItemStack stack = new ItemStack(Registry.ITEM.getRandom(random), random.nextInt(3));
				if (combined.isValid(slot, stack)) {
					combined.setStack(slot, stack);
				}
			}
			System.out.println(value + ":" + Arrays.toString(slots));
		}

		System.out.println(combined);
	}
}
