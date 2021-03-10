package io.github.astrarre.gui.v0.api.base;

import io.github.astrarre.gui.v0.api.Drawable;
import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.networking.v0.api.io.Input;
import io.github.astrarre.networking.v0.api.io.Output;
import io.github.astrarre.rendering.v0.api.util.Polygon;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public abstract class RectangularDrawable extends Drawable {
	public final int width;
	public final int height;

	protected RectangularDrawable(DrawableRegistry.Entry id, int width, int height) {
		super(id);
		this.width = width;
		this.height = height;
		this.setBoundsProtected(Polygon.rectangle(width, height));
	}

	@Environment(EnvType.CLIENT)
	protected RectangularDrawable(DrawableRegistry.Entry id, Input input) {
		super(id);
		this.width = input.readInt();
		this.height = input.readInt();
	}

	@Override
	protected void write0(RootContainer container, Output output) {
		output.writeInt(this.width);
		output.writeInt(this.height);
	}

	@Override
	public void setBounds(Polygon polygon) {
		throw new UnsupportedOperationException();
	}
}
