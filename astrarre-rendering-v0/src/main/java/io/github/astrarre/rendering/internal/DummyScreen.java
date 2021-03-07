package io.github.astrarre.rendering.internal;


import java.util.List;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

/**
 * get access to Screen's utility methods to maintain compat with mods and less update burden
 */
public final class DummyScreen extends Screen {
	public static final DummyScreen INSTANCE = new DummyScreen();
	private DummyScreen() {
		super(new LiteralText("astrarre dummy screen"));
	}

	@Override
	public void renderTooltip(MatrixStack matrices, ItemStack stack, int x, int y) {
		super.renderTooltip(matrices, stack, x, y);
	}
}
