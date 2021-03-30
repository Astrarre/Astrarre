package io.github.astrarre.rendering.internal;


import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;

/**
 * get access to Screen's utility methods to maintain compat with mods and less update burden
 */
public final class DummyScreen extends Screen {
	/**
	 * this is the maximum 'guaranteed' window in which you can render for GUIs
	 * In auto gui mode (in video settings) will rescale the coordinate grid to ensure that this 'window' in the center of the screen is always visible.
	 * For normal GUIs (centered guis, like inventories for example): it's recommended to use this scale.
	 */
	public static final int MIN_WIDTH = 320, MIN_HEIGHT = 240;

	public static final DummyScreen INSTANCE = new DummyScreen();
	private DummyScreen() {
		super(new LiteralText("astrarre dummy screen"));
	}

	@Override
	public void renderTooltip(MatrixStack matrices, ItemStack stack, int x, int y) {
		super.renderTooltip(matrices, stack, x, y);
	}
}
