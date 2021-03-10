package io.github.astrarre.gui.v0.api.statik;

import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.gui.v0.api.base.RectangularDrawable;
import io.github.astrarre.gui.v0.api.panel.CenteringPanel;
import io.github.astrarre.networking.v0.api.io.Input;
import io.github.astrarre.rendering.v0.api.Graphics3d;
import io.github.astrarre.rendering.v0.api.util.Polygon;
import io.github.astrarre.util.v0.api.Id;

/**
 * the standard inventory background
 */
public final class BeveledRectangle extends RectangularDrawable {
	private static final DrawableRegistry.Entry ENTRY = DrawableRegistry.register(Id.create("astrarre-gui-v0", "beveled_rectangle"),
			BeveledRectangle::new);

	public BeveledRectangle(CenteringPanel panel) {
		this(panel.width, panel.height);
	}

	public BeveledRectangle(RectangularDrawable drawable) {
		this(drawable.width, drawable.height);
	}

	private BeveledRectangle(Input input) {
		this(input.readInt(), input.readInt());
	}

	public BeveledRectangle(int width, int height) {
		this(ENTRY, width, height);
	}

	private BeveledRectangle(DrawableRegistry.Entry id, int width, int height) {
		super(id, width, height);
	}

	@Override
	protected void render0(RootContainer container, Graphics3d graphics, float tickDelta) {
		// the background part
		graphics.fillRect(2, 2, this.width - 5, this.height - 5, 0xffc6c6c6);
		// the top shiny part
		graphics.fillRect(2, 1, this.width - 6, 2, 0xffffffff);
		// the left shiny part
		graphics.fillRect(1, 2, 2, this.height - 6, 0xffffffff);
		// that one pixel in the top left
		graphics.fillRect(3, 3, 1, 1, 0xffffffff);
		// the right shadow
		graphics.fillRect(this.width - 4, 3, 2, this.height - 6, 0xff555555);
		// the bottom shadow
		graphics.fillRect(3, this.height - 4, this.width - 6, 2, 0xff555555);
		// that one pixel in the bottom right
		graphics.fillRect(this.width - 5, this.height - 5, 1, 1, 0xff555555);
		// the border
		graphics.fillRect(0, 2, 1, this.height-6, 0xff000000);
		graphics.fillRect(1, 1,1, 1, 0xff000000);
		graphics.fillRect(2, 0, this.width-6, 1, 0xff000000);
		graphics.fillRect(this.width - 4, 1, 1, 1, 0xff000000);
		graphics.fillRect(this.width - 3, 2, 1, 1, 0xff000000);
		graphics.fillRect(this.width - 2, 3, 1, this.height - 6, 0xff000000);
		graphics.fillRect(1, this.height-4, 1, 1, 0xff000000);
		graphics.fillRect(2, this.height-3, 1, 1, 0xff000000);
		graphics.fillRect(3, this.height-2, this.width - 6, 1, 0xff000000);
		graphics.fillRect(this.width-3, this.height-3, 1, 1, 0xff000000);
	}


	@Override
	public void setBounds(Polygon polygon) {
		throw new UnsupportedOperationException();
	}
}
