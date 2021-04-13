package io.github.astrarre.rendering.v0.fabric;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.util.math.MatrixStack;

public interface FabricGraphics {
	MatrixStack getTransformationMatrix();

	/**
	 * unused, will be at some point hopefully
	 */
	default BufferBuilder getBuilder() {
		return Tessellator.getInstance().getBuffer();
	}
}
