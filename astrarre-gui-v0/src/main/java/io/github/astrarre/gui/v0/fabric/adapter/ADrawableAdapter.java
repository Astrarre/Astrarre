package io.github.astrarre.gui.v0.fabric.adapter;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.astrarre.gui.v0.api.ADrawable;
import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.gui.v0.api.access.Interactable;
import io.github.astrarre.gui.v0.api.access.Tickable;
import io.github.astrarre.gui.v0.api.graphics.GuiGraphics;
import io.github.astrarre.rendering.v0.fabric.FabricGraphics;

import net.minecraft.client.util.math.MatrixStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public abstract class ADrawableAdapter<T extends net.minecraft.client.gui.Drawable> extends ADrawable implements Interactable, Tickable {
	@Environment (EnvType.CLIENT) protected T drawable;

	private int mx = 1_000_000, my = 1_000_000;
	private int tick;

	/**
	 * the drawable must be centered at 0, 0
	 */
	public ADrawableAdapter(DrawableRegistry.Entry id) {
		super(id);
	}

	@Override
	protected void render0(RootContainer container, GuiGraphics graphics, float tickDelta) {
		if (graphics instanceof FabricGraphics) {
			this.drawable.render(((FabricGraphics) graphics).getTransformationMatrix(), this.mx, this.my, tickDelta);
		}

	}

	/**
	 * TickableElement was deleted, so all drawable adapters must call the appropriate tick function
	 */
	@Override
	public abstract void tick(RootContainer container);

	@Override
	public boolean isHovering(RootContainer container, double mouseX, double mouseY) {
		this.mx = (int) mouseX;
		this.my = (int) mouseY;
		return true;
	}
}
