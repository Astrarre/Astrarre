package io.github.astrarre.gui.internal.vanilla;

import io.github.astrarre.rendering.internal.DummyScreen;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class DefaultHandledScreen extends HandledScreen<DefaultScreenHandler> {

	public DefaultHandledScreen(DefaultScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);
		this.backgroundWidth = DummyScreen.MIN_WIDTH;
		this.backgroundHeight = DummyScreen.MIN_HEIGHT;
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		super.render(matrices, mouseX, mouseY, delta);
		this.drawMouseoverTooltip(matrices, mouseX, mouseY);
	}

	@Override
	protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
	}

	@Override
	protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
	}
}
