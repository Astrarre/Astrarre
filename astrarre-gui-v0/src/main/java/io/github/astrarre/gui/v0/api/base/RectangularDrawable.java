package io.github.astrarre.gui.v0.api.base;

import io.github.astrarre.gui.v0.api.Drawable;
import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.networking.v0.api.io.Input;
import io.github.astrarre.networking.v0.api.io.Output;
import io.github.astrarre.rendering.v0.api.util.Polygon;

public abstract class RectangularDrawable extends Drawable {
	public final int width;
	public final int height;

	protected RectangularDrawable(RootContainer rootContainer, DrawableRegistry.Entry id, int width, int height) {
		super(rootContainer, id);
		this.width = width;
		this.height = height;
		this.setBoundsProtected(Polygon.rectangle(width, height));
	}

	protected RectangularDrawable(RootContainer rootContainer, DrawableRegistry.Entry id, Input input) {
		super(rootContainer, id);
		this.width = input.readInt();
		this.height = input.readInt();
	}

	@Override
	protected void write0(Output output) {
		output.writeInt(this.width);
		output.writeInt(this.height);
	}

	@Override
	public void setBounds(Polygon polygon) {
		throw new UnsupportedOperationException();
	}
}
