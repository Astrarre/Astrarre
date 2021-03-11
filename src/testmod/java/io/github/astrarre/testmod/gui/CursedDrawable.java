package io.github.astrarre.testmod.gui;

import io.github.astrarre.gui.v0.api.DelegateDrawable;
import io.github.astrarre.gui.v0.api.Drawable;
import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.gui.v0.api.access.Tickable;
import io.github.astrarre.networking.v0.api.io.Input;
import io.github.astrarre.rendering.v0.api.Graphics3d;
import io.github.astrarre.rendering.v0.api.Transformation;
import io.github.astrarre.rendering.v0.api.util.Close;
import io.github.astrarre.util.v0.api.Id;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class CursedDrawable extends DelegateDrawable implements Tickable {
	private static final DrawableRegistry.Entry ENTRY = DrawableRegistry.register(Id.create("testmod", "cursed"), CursedDrawable::new);
	public CursedDrawable(Drawable delegate) {
		super(ENTRY, delegate);
	}

	@Environment(EnvType.CLIENT)
	private CursedDrawable(Input input) {
		super(ENTRY, input);
	}

	@Override
	protected void render0(RootContainer container, Graphics3d graphics, float tickDelta) {
		Close c = graphics.applyTransformation(Transformation.translate(-8, -8, 0));
		super.render0(container, graphics, tickDelta);
		c.close();
	}

	@Override
	public void tick(RootContainer container) {
		int tick = container.getTick();
		this.setTransformation(Transformation.rotate(0, tick/4f, tick/4f));
	}
}
