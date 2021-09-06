package io.github.astrarre.gui.internal.std;

import io.github.astrarre.gui.internal.GuiInternal;
import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;

public class StandardScreenHandler extends ScreenHandler {
	public StandardScreenHandler(int syncId) {
		super(GuiInternal.HANDLER_TYPE, syncId);
	}

	public StandardScreenHandler(int syncId, PlayerInventory inventory) {
		super(GuiInternal.HANDLER_TYPE, syncId);
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return true;
	}
}
