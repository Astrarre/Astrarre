package io.github.astrarre.testmod.gui.thing;

import io.github.astrarre.gui.v0.api.Drawable;
import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.networking.v0.api.io.Input;
import io.github.astrarre.networking.v0.api.io.Output;
import io.github.astrarre.rendering.v0.api.Graphics3d;
import io.github.astrarre.util.v0.api.Id;

public final class Cube extends Drawable {
	private static final DrawableRegistry.Entry ENTRY = DrawableRegistry.register(Id.create("testmod", "cube"), Cube::new);
	public final int color;

	public Cube(int color) {
		super(ENTRY);
		this.color = color;
	}

	public Cube(Input input) {
		super(ENTRY);
		this.color = input.readInt();
	}

	@Override
	protected void render0(RootContainer container, Graphics3d graphics, float tickDelta) {
		graphics.fillRect(0, 0, 0, 0, 10, 0, 10, 10, 0, 10, 0, 0, this.color);
		graphics.fillRect(0, 0, 0, 10, 0, 0, 10, 0, 10, 0, 0, 10, this.color);
		graphics.fillRect(0, 0, 0, 0, 10, 0, 0, 10, 10, 0, 0, 10, this.color);

		graphics.fillRect(0, 0, 10, 0, 10, 0, 10, 10, 10, 10, 0, 10, this.color);
		graphics.fillRect(0, 10, 0, 10, 10, 0, 10, 10, 10, 0, 10, 10, this.color);
		graphics.fillRect(10, 0, 0, 10, 10, 0, 10, 10, 10, 10, 0, 10, this.color);
	}

	@Override
	protected void write0(RootContainer container, Output output) {
		output.writeInt(this.color);
	}
}
