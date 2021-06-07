package io.github.astrarre.gui.internal.vanilla;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class NilSlot extends Slot {
	public static final NilSlot SLOT = new NilSlot();
	public NilSlot() {
		super(new Inventory() {
			@Override public int size() {return 1;}
			@Override public boolean isEmpty() {return true;}
			@Override public ItemStack getStack(int slot) {return ItemStack.EMPTY;}
			@Override public ItemStack removeStack(int slot, int amount) {return ItemStack.EMPTY;}
			@Override public ItemStack removeStack(int slot) {return ItemStack.EMPTY;}
			@Override public void setStack(int slot, ItemStack stack) {}
			@Override public void markDirty() {}
			@Override public boolean canPlayerUse(PlayerEntity player) {return false;}
			@Override public void clear() {}
		}, 0, 0, 0);
	}

	@Override
	public boolean isEnabled() {
		return false;
	}
}
