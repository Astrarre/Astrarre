package io.github.astrarre.gui.internal.std;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class StandardScreen extends HandledScreen<StandardScreenHandler> {
	public StandardScreen(StandardScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);
	}

	@Override
	protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
	}

	@Override
	protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
		// crab
	}
}
