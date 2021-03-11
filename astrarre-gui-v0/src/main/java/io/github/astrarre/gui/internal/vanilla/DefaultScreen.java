package io.github.astrarre.gui.internal.vanilla;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

public class DefaultScreen extends Screen {
	public DefaultScreen() {
		super(new LiteralText("dummy_text"));
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		super.render(matrices, mouseX, mouseY, delta);
	}

	@Override
	public void renderBackground(MatrixStack matrices) {
	}

	@Override
	public void renderBackground(MatrixStack matrices, int vOffset) {
	}

	@Override
	public void renderBackgroundTexture(int vOffset) {
	}
}
