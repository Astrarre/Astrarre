package io.github.astrarre.transfer.internal.fabric.inventory;

import java.util.Set;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public interface InventoryDelegate extends Inventory {
	Inventory getDelegate();

	@Override
	default int size() {
		return this.getDelegate().size();
	}

	@Override
	default boolean isEmpty() {
		return this.getDelegate().isEmpty();
	}

	@Override
	default ItemStack getStack(int slot) {
		return this.getDelegate().getStack(slot);
	}

	@Override
	default ItemStack removeStack(int slot, int amount) {
		return this.getDelegate().removeStack(slot, amount);
	}

	@Override
	default ItemStack removeStack(int slot) {
		return this.getDelegate().removeStack(slot);
	}

	@Override
	default void setStack(int slot, ItemStack stack) {
		this.getDelegate().setStack(slot, stack);
	}

	@Override
	default void markDirty() {
		this.getDelegate().markDirty();
	}

	@Override
	default boolean canPlayerUse(PlayerEntity player) {
		return this.getDelegate().canPlayerUse(player);
	}

	@Override
	default void clear() {
		this.getDelegate().clear();
	}

	@Override
	default int getMaxCountPerStack() {
		return this.getDelegate().getMaxCountPerStack();
	}

	@Override
	default void onOpen(PlayerEntity player) {
		this.getDelegate().onOpen(player);
	}

	@Override
	default void onClose(PlayerEntity player) {
		this.getDelegate().onClose(player);
	}

	@Override
	default boolean isValid(int slot, ItemStack stack) {
		return this.getDelegate().isValid(slot, stack);
	}

	@Override
	default int count(Item item) {
		return this.getDelegate().count(item);
	}

	@Override
	default boolean containsAny(Set<Item> items) {
		return this.getDelegate().containsAny(items);
	}
}
