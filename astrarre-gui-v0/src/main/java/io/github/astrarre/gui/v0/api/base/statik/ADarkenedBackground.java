package io.github.astrarre.gui.v0.api.base.statik;

import io.github.astrarre.gui.v0.api.Drawable;
import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.networking.v0.api.io.Input;
import io.github.astrarre.networking.v0.api.io.Output;
import io.github.astrarre.rendering.v0.api.Graphics3d;
import io.github.astrarre.util.v0.api.Id;

public class ADarkenedBackground extends Drawable {
	private static final DrawableRegistry.Entry ENTRY = DrawableRegistry.register(Id.create("astrarre-gui-v0", "darkened_background"), ADarkenedBackground::new);
	private int width, height;

	public ADarkenedBackground() {
		super(ENTRY);
	}

	public ADarkenedBackground(Input input) {
		this();
	}

	@Override
	protected void onAdded(RootContainer container) {
		super.onAdded(container);
		container.addResizeListener((width, height) -> {
			this.width = width;
			this.height = height;
		});
	}

	@Override
	protected void render0(RootContainer container, Graphics3d graphics, float tickDelta) {
		graphics.fillGradient(this.width, this.height, -1072689136, -804253680);
	}

	@Override
	protected void write0(RootContainer container, Output output) {
	}

	public static void init() {}
}
