package io.github.astrarre.gui.v0.api.panel;

import io.github.astrarre.gui.v0.api.Drawable;
import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.gui.v0.api.base.AggregateDrawable;
import io.github.astrarre.networking.v0.api.io.Input;
import io.github.astrarre.rendering.v0.api.Graphics3d;
import io.github.astrarre.util.v0.api.Id;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class Panel extends AggregateDrawable {
	private static final DrawableRegistry.Entry PANEL = DrawableRegistry.register(Id.create("astrarre-gui-v0", "panel"), Panel::new);

	public Panel() {
		this(PANEL);
	}

	protected Panel(DrawableRegistry.Entry entry) {
		super(entry);
	}

	@Environment (EnvType.CLIENT)
	private Panel(Input input) {
		this(PANEL, input);
	}

	@Environment (EnvType.CLIENT)
	protected Panel(DrawableRegistry.Entry entry, Input input) {
		super(entry, input);
	}

	public static void init() {}

	@Override
	protected void render0(RootContainer container, Graphics3d graphics, float tickDelta) {
		for (Drawable drawable : this.drawables) {
			drawable.render(container, graphics, tickDelta);
		}
	}
}
