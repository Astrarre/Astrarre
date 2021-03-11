package io.github.astrarre.testmod.gui.thing;

import io.github.astrarre.gui.v0.api.Drawable;
import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.gui.v0.api.access.Tickable;
import io.github.astrarre.gui.v0.api.base.AggregateDrawable;
import io.github.astrarre.rendering.v0.api.Graphics3d;

public class OrbitalSystem extends AggregateDrawable implements Tickable {
	public OrbitalSystem(DrawableRegistry.Entry id) {
		super(id);
	}

	@Override
	protected void render0(RootContainer container, Graphics3d graphics, float tickDelta) {
		for (Drawable drawable : this.drawables) {
			drawable.render(container, graphics, tickDelta);
		}
	}

	@Override
	public void tick(RootContainer container) {
		for (int i = 1; i < this.drawables.size(); i++) {
			Drawable drawable = this.drawables.get(0);

		}
	}
}
