package io.github.astrarre.gui.internal.std;

import io.github.astrarre.gui.internal.GuiInternal;
import io.github.astrarre.gui.internal.slot.SlotAdapter;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class StandardScreenHandler extends ScreenHandler {
	public StandardScreenHandler(int syncId) {
		super(GuiInternal.HANDLER_TYPE, syncId);
	}

	public StandardScreenHandler(int syncId, PlayerInventory inventory) {
		super(GuiInternal.HANDLER_TYPE, syncId);
	}

	@Override
	public ItemStack transferSlot(PlayerEntity player, int index) {
		Slot slot = this.slots.get(index);
		if(slot instanceof SlotAdapter m) {
			ItemStack stack = m.transferToLinked();
			return slot.getStack() == stack ? ItemStack.EMPTY : stack; // avoid infinite loop
		}

		return ItemStack.EMPTY;
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return true;
	}
}
