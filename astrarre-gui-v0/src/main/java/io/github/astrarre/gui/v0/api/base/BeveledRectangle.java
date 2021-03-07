package io.github.astrarre.gui.v0.api.base;

import io.github.astrarre.gui.v0.api.Drawable;
import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.networking.v0.api.io.Input;
import io.github.astrarre.networking.v0.api.io.Output;
import io.github.astrarre.rendering.v0.api.Graphics3d;
import io.github.astrarre.rendering.v0.api.util.Polygon;
import io.github.astrarre.util.v0.api.Id;

/**
 * the standard inventory background
 */
public class BeveledRectangle extends Drawable {
	private static final DrawableRegistry.Entry ENTRY = DrawableRegistry.register(Id.newInstance("astrarre-gui-v0", "beveled_rectangle"),
			BeveledRectangle::new);
	protected final int width, height;

	public BeveledRectangle(RootContainer container, CenteringPanel panel) {
		this(container, panel.width, panel.height);
	}

	protected BeveledRectangle(RootContainer container, Input input) {
		this(container, input.readInt(), input.readInt());
	}

	public BeveledRectangle(RootContainer container, int width, int height) {
		this(container, ENTRY, width, height);
	}

	protected BeveledRectangle(RootContainer rootContainer, DrawableRegistry.Entry id, int width, int height) {
		super(rootContainer, id);
		this.setBoundsProtected(new Polygon.Builder(4).addVertex(0, 0).addVertex(0, height).addVertex(width, height).addVertex(width, 0).build());
		this.width = width;
		this.height = height;
	}

	@Override
	protected void render0(Graphics3d graphics, float tickDelta) {
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

		this.getBounds().walk((x1, y1, x2, y2) -> graphics.drawLine(x1, y1, x2, y2, 0xffaaaaaa));
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
