package io.github.astrarre.gui.v0.api.base;

import io.github.astrarre.gui.v0.api.Drawable;
import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.networking.v0.api.io.Input;
import io.github.astrarre.networking.v0.api.io.Output;
import io.github.astrarre.rendering.v0.api.Graphics3d;
import io.github.astrarre.util.v0.api.Id;

public class DarkenedBackground extends Drawable {
	private static final DrawableRegistry.Entry ENTRY = DrawableRegistry.register(Id.newInstance("astrarre-gui-v0", "darkened_background"), DarkenedBackground::new);
	private int width, height;

	public DarkenedBackground(RootContainer rootContainer) {
		super(rootContainer, ENTRY);
	}

	public DarkenedBackground(RootContainer container, Input input) {
		this(container);
		this.rootContainer.addResizeListener((width, height) -> {
			this.width = width;
			this.height = height;
		});
	}

	@Override
	protected void render0(Graphics3d graphics, float tickDelta) {
		graphics.fillGradient(this.width, this.height, -1072689136, -804253680);
	}

	@Override
	protected void write0(Output output) {
	}
}
