package io.github.astrarre.transfer.internal.compat;

import java.util.Set;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * implements {@link Inventory#isValid(int, ItemStack)} for PlayerInventory
 */
public class ProperPlayerInventory implements Inventory {
	public final PlayerInventory inventory;

	public ProperPlayerInventory(PlayerInventory inventory) {
		this.inventory = inventory;
	}

	@Override
	public boolean isValid(int slot, ItemStack stack) {
		if(slot < this.inventory.main.size()) {
			return true;
		} else if(slot < this.inventory.armor.size() + this.inventory.main.size()) {
			Item i = stack.getItem();
			return i instanceof ArmorItem && ((ArmorItem) i).getSlotType().getEntitySlotId() == (slot - this.inventory.armor.size());
		}
		return this.inventory.isValid(slot, stack);
	}

	@Override
	public int size() {
		return this.inventory.size();
	}

	@Override
	public boolean isEmpty() {
		return this.inventory.isEmpty();
	}

	@Override
	public ItemStack getStack(int slot) {
		return this.inventory.getStack(slot);
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		return this.inventory.removeStack(slot, amount);
	}

	@Override
	public ItemStack removeStack(int slot) {
		return this.inventory.removeStack(slot);
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		this.inventory.setStack(slot, stack);
	}

	@Override
	public int getMaxCountPerStack() {
		return this.inventory.getMaxCountPerStack();
	}

	@Override
	public void markDirty() {
		this.inventory.markDirty();
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return this.inventory.canPlayerUse(player);
	}

	@Override
	public void onOpen(PlayerEntity player) {
		this.inventory.onOpen(player);
	}

	@Override
	public void onClose(PlayerEntity player) {
		this.inventory.onClose(player);
	}

	@Override
	public int count(Item item) {
		return this.inventory.count(item);
	}

	@Override
	public boolean containsAny(Set<Item> items) {
		return this.inventory.containsAny(items);
	}

	@Override
	public void clear() {
		this.inventory.clear();
	}
}
