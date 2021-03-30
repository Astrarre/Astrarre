package io.github.astrarre.gui.internal.vanilla;

import io.github.astrarre.gui.internal.AstrarreInitializer;
import io.github.astrarre.gui.internal.access.ExtraSlotAccess;
import io.github.astrarre.gui.internal.access.ScreenRootAccess;
import io.github.astrarre.gui.internal.access.SlotAddAccess;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class DefaultScreenHandler extends ScreenHandler {
	public DefaultScreenHandler(int syncId) {
		super(AstrarreInitializer.PANEL_SCREEN, syncId);
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return true;
	}

	@Override
	public ItemStack transferSlot(PlayerEntity player, int index) {
		Slot slot = this.slots.get(index);
		if(slot instanceof ExtraSlotAccess) {
			return ((ExtraSlotAccess) slot).transferSlot(((ScreenRootAccess)this).getRoot());
		}
		return super.transferSlot(player, index);
	}
}
