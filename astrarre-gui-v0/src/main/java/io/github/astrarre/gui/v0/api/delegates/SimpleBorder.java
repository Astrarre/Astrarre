package io.github.astrarre.gui.v0.api.delegates;

import io.github.astrarre.gui.v0.api.Drawable;
import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.gui.v0.api.base.DelegateDrawable;
import io.github.astrarre.networking.v0.api.io.Input;
import io.github.astrarre.rendering.v0.api.Graphics3d;
import io.github.astrarre.util.v0.api.Id;

/**
 * makes a black line around the bounds of the delegate drawable
 */
public class SimpleBorder extends DelegateDrawable {
	private static final DrawableRegistry.Entry ENTRY = DrawableRegistry.register(Id.create("astrarre-gui-v0", "simple_border"), SimpleBorder::new);
	public SimpleBorder(RootContainer rootContainer, Drawable delegate) {
		super(rootContainer, ENTRY, delegate);
	}

	protected SimpleBorder(RootContainer container, Input input) {
		super(container, ENTRY, input);
	}

	@Override
	protected void render0(Graphics3d graphics, float tickDelta) {
		super.render0(graphics, tickDelta);
		this.getBounds().walk((x1, y1, x2, y2) -> graphics.drawLine(x1, y1, x2, y2, 0xff000000));
	}
}
