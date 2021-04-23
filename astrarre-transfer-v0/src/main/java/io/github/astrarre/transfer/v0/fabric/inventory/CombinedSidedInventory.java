package io.github.astrarre.transfer.v0.fabric.inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.Nullable;

import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;

/**
 * an inventory of inventories but with sides
 */
public final class CombinedSidedInventory extends CombinedInventory implements SidedInventory {
	protected final Map<Direction, Inventory> sides;
	protected final Map<Direction, int[]> cache;

	/**
	 * @param cache true if the sizes of the inventories do not change
	 */
	public CombinedSidedInventory(Map<Direction, Inventory> sides, boolean cache) {
		super(new ArrayList<>(new HashSet<>(sides.values())), cache);
		this.sides = sides;
		if(cache) {
			this.cache = new EnumMap<>(Direction.class);
		} else {
			this.cache = null;
		}
	}

	@Override
	public int[] getAvailableSlots(Direction side) {
		Inventory inventory = this.sides.get(side);
		if(inventory == null) {
			return ArrayUtils.EMPTY_INT_ARRAY;
		}

		if(this.cache != null) {
			int[] cached = this.cache.get(side);
			if(cached != null) {
				return cached;
			}
		}

		int[] slots = new int[inventory.size()];
		int offset = 0;
		for (Inventory i : this.inventories) {
			if(i == inventory) {
				break;
			}
			offset += i.size();
		}

		for (int i = 0; i < inventory.size(); i++) {
			slots[i] = i + offset;
		}

		if(this.cache != null) {
			this.cache.put(side, slots);
		}

		return slots;
	}

	@Override
	public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
		SlotAccess access = this.get(slot);
		Inventory inventory = access.inventory;
		if(inventory instanceof SidedInventory) {
			return ((SidedInventory) inventory).canInsert(slot, stack, null);
		}
		return true;
	}

	@Override
	public boolean canExtract(int slot, ItemStack stack, Direction dir) {
		SlotAccess access = this.get(slot);
		Inventory inventory = access.inventory;
		if(inventory instanceof SidedInventory) {
			return ((SidedInventory) inventory).canExtract(slot, stack, null);
		}
		return true;
	}
}
