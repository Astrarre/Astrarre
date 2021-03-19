package io.github.astrarre.gui.internal.vanilla;

import io.github.astrarre.gui.internal.AstrarreInitializer;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;

public class DefaultScreenHandler extends ScreenHandler {
	public DefaultScreenHandler(int syncId) {
		super(AstrarreInitializer.PANEL_SCREEN, syncId);
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return true;
	}
}
