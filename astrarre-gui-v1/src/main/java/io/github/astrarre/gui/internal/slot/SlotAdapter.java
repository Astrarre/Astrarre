package io.github.astrarre.gui.internal.slot;

import io.github.astrarre.gui.v1.api.component.slot.ASlot;
import io.github.astrarre.gui.v1.api.component.slot.SlotKey;
import io.github.astrarre.itemview.v0.fabric.ItemKey;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class SlotAdapter extends Slot {
	final SlotKey key;
	final Object this_;

	public SlotAdapter(Inventory inventory, int index, SlotKey key) {
		this(inventory, index, key, null);
	}

	public SlotAdapter(Inventory inventory, int index, SlotKey key, Object this_) {
		super(inventory, index, -1024, -1024);
		this.key = key;
		this.this_ = this_;
	}

	@Override
	public boolean isEnabled() {
		return this.this_ instanceof ASlot a && a.isEnabled();
	}

	@Environment(EnvType.CLIENT)
	public ASlot slot() {
		return (ASlot) this.this_;
	}

	public ItemStack transferToLinked() {
		return this.key.transferToLinked();
	}

	@Override
	public ItemStack getStack() {
		return this.key.getStack();
	}

	@Override
	public boolean canInsert(ItemStack stack) {
		return this.key.isValid(ItemKey.ofStack(stack));
	}

	@Override
	public void setStack(ItemStack stack) {
		this.key.setStack(stack);
		this.markDirty();
	}

	@Override
	public void markDirty() {
		this.key.markDirty();
	}

	@Override
	public int getMaxItemCount() {
		return this.key.getMaxCount(ItemKey.EMPTY);
	}

	@Override
	public int getMaxItemCount(ItemStack stack) {
		return this.key.getMaxCount(ItemKey.ofStack(stack));
	}

	@Override
	public ItemStack takeStack(int amount) {
		ItemKey key = ItemKey.ofStack(this.getStack());
		int count = this.key.extract(key, amount, false);
		return key.createItemStack(count);
	}

	@Override
	public ItemStack insertStack(ItemStack stack, int count) {
		int c = this.key.insert(ItemKey.ofStack(stack), count, false);
		stack.decrement(c);
		return stack;
	}

	@Override
	public boolean canTakeItems(PlayerEntity playerEntity) {
		return this.key.extract(ItemKey.ofStack(this.getStack()), 1, true) != 0;
	}
}
