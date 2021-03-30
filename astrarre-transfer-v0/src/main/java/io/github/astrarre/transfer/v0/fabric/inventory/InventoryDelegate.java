package io.github.astrarre.transfer.v0.fabric.inventory;

import java.util.Set;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public interface InventoryDelegate extends Inventory {
	Inventory getInventoryDelegate();

	@Override
	default int size() {
		return this.getInventoryDelegate().size();
	}

	@Override
	default boolean isEmpty() {
		return this.getInventoryDelegate().isEmpty();
	}

	@Override
	default ItemStack getStack(int slot) {
		return this.getInventoryDelegate().getStack(slot);
	}

	@Override
	default ItemStack removeStack(int slot, int amount) {
		return this.getInventoryDelegate().removeStack(slot, amount);
	}

	@Override
	default ItemStack removeStack(int slot) {
		return this.getInventoryDelegate().removeStack(slot);
	}

	@Override
	default void setStack(int slot, ItemStack stack) {
		this.getInventoryDelegate().setStack(slot, stack);
	}

	@Override
	default int getMaxCountPerStack() {
		return this.getInventoryDelegate().getMaxCountPerStack();
	}

	@Override
	default void markDirty() {
		this.getInventoryDelegate().markDirty();
	}

	@Override
	default boolean canPlayerUse(PlayerEntity player) {
		return this.getInventoryDelegate().canPlayerUse(player);
	}

	@Override
	default void onOpen(PlayerEntity player) {
		this.getInventoryDelegate().onOpen(player);
	}

	@Override
	default void onClose(PlayerEntity player) {
		this.getInventoryDelegate().onClose(player);
	}

	@Override
	default boolean isValid(int slot, ItemStack stack) {
		return this.getInventoryDelegate().isValid(slot, stack);
	}

	@Override
	default int count(Item item) {
		return this.getInventoryDelegate().count(item);
	}

	@Override
	default boolean containsAny(Set<Item> items) {
		return this.getInventoryDelegate().containsAny(items);
	}

	@Override
	default void clear() {
		this.getInventoryDelegate().clear();
	}
}
