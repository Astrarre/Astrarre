package io.github.astrarre.gui.v0.fabric.adapter;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.astrarre.gui.v0.api.Drawable;
import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.gui.v0.api.access.Interactable;
import io.github.astrarre.rendering.internal.MatrixGraphics;
import io.github.astrarre.rendering.v0.api.Graphics3d;
import io.github.astrarre.rendering.v0.api.graphics.DelegateGraphics;
import io.github.astrarre.rendering.v0.api.util.Polygon;

import net.minecraft.client.gui.screen.TickableElement;
import net.minecraft.client.util.math.MatrixStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public abstract class DrawableAdapter<T extends net.minecraft.client.gui.Drawable> extends Drawable implements Interactable {
	@Environment (EnvType.CLIENT) protected T drawable;

	private int mx = 1_000_000, my = 1_000_000;
	private int tick;

	/**
	 * the drawable must be centered at 0, 0
	 */
	public DrawableAdapter(DrawableRegistry.Entry id) {
		super(id);
	}

	@Override
	protected void render0(RootContainer container, Graphics3d graphics, float tickDelta) {
		Graphics3d matrix = DelegateGraphics.resolve(graphics);
		if (matrix instanceof MatrixGraphics) {
			// todo stop this terrible hack once mojang decides it's time to move everything over to MatrixStack instead of RenderSystem's
			//  push/popMatrix
			RenderSystem.pushMatrix();
			RenderSystem.multMatrix(((MatrixGraphics) matrix).matrices.peek().getModel());
			this.drawable.render(new MatrixStack(), this.mx, this.my, tickDelta);
			RenderSystem.color4f(1f, 1f, 1f, 1f);
			RenderSystem.popMatrix();
		}

		int tick = container.getTick();
		if (tick != this.tick) {
			this.tick = tick;
			this.mx = 1_000_000;
			this.my = 1_000_000;
			if (this.drawable instanceof TickableElement) {
				((TickableElement) this.drawable).tick();
			}
		}
	}

	@Override
	public boolean isHovering(RootContainer container, double mouseX, double mouseY) {
		this.mx = (int) mouseX;
		this.my = (int) mouseY;
		return true;
	}
}
