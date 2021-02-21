package io.github.astrarre.rendering.internal;

import io.github.astrarre.rendering.v0.api.Graphics3d;
import io.github.astrarre.rendering.v0.fabric.MatrixGraphics;
import io.github.astrarre.v0.client.texture.Sprite;

import net.minecraft.client.util.math.MatrixStack;

public class AstrarreMatrixGraphics extends MatrixGraphics implements Graphics3d {
	public AstrarreMatrixGraphics(MatrixStack matrices) {
		super(matrices);
	}

	@Override
	public void drawSprite(Sprite sprite) {
		this.drawSprite((net.minecraft.client.texture.Sprite) sprite);
	}
}
