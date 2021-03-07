package io.github.astrarre.gui.internal.vanilla;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public final class DefaultHandledScreen extends HandledScreen<DefaultScreenHandler> {
	/**
	 * this is the maximum 'guaranteed' window in which you can render for GUIs
	 * In auto gui mode (in video settings) will rescale the coordinate grid to ensure that this 'window' in the center of the screen is always visible.
	 * For normal GUIs (centered guis, like inventories for example): it's recommended to use this scale.
	 */
	public static final int MIN_WIDTH = 320, MIN_HEIGHT = 240;
	public DefaultHandledScreen(DefaultScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);
		this.backgroundWidth = MIN_WIDTH;
		this.backgroundHeight = MIN_HEIGHT;
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
