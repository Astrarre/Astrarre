package io.github.astrarre.gui.v0.api.base.statik;

import io.github.astrarre.gui.v0.api.ADrawable;
import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.gui.v0.api.graphics.GuiGraphics;
import io.github.astrarre.rendering.v0.api.util.Polygon;
import io.github.astrarre.util.v0.api.Id;

/**
 * the standard inventory background. If you want it to resize with the drawable
 */
public final class ABeveledRectangle extends ADrawable {
	private static final DrawableRegistry.Entry ENTRY = DrawableRegistry.register(Id.create("astrarre-gui-v0", "beveled_rectangle"),
			ABeveledRectangle::new);

	protected final float width, height;

	/**
	 * captures the width and height of the panel
	 */
	public ABeveledRectangle(ADrawable panel) {
		super(ENTRY);
		Polygon enclosing = panel.getBounds().getEnclosing();
		this.setBounds(enclosing);
		this.width = enclosing.getX(2);
		this.height = enclosing.getY(2);
	}

	private ABeveledRectangle(NBTagView input) {
		this(input.getFloat("width"), input.getFloat("height"));
	}

	public ABeveledRectangle(float width, float height) {
		this(ENTRY, width, height);
	}

	private ABeveledRectangle(DrawableRegistry.Entry id, float width, float height) {
		super(id);
		this.setBounds(Polygon.rectangle(width, height));
		this.width = width;
		this.height = height;
	}

	@Override
	protected void render0(RootContainer container, GuiGraphics graphics, float tickDelta) {
		drawBevel(graphics, this.width, this.height);
	}

	@Override
	protected void write0(RootContainer container, NBTagView.Builder output) {
		output.putFloat("width", this.width);
		output.putFloat("height", this.height);
	}

	public static void drawBevel(GuiGraphics graphics, float width, float height) {
		// the background part
		graphics.fillRect(2, 2, width - 5, height - 5, 0xffc6c6c6);
		// the top shiny part
		graphics.fillRect(2, 1, width - 6, 2, 0xffffffff);
		// the left shiny part
		graphics.fillRect(1, 2, 2, height - 6, 0xffffffff);
		// that one pixel in the top left
		graphics.fillRect(3, 3, 1, 1, 0xffffffff);
		// the right shadow
		graphics.fillRect(width - 4, 3, 2, height - 6, 0xff555555);
		// the bottom shadow
		graphics.fillRect(3, height - 4, width - 6, 2, 0xff555555);
		// that one pixel in the bottom right
		graphics.fillRect(width - 5, height - 5, 1, 1, 0xff555555);
		// the border
		graphics.fillRect(0, 2, 1, height-6, 0xff000000);
		graphics.fillRect(1, 1,1, 1, 0xff000000);
		graphics.fillRect(2, 0, width-6, 1, 0xff000000);
		graphics.fillRect(width - 4, 1, 1, 1, 0xff000000);
		graphics.fillRect(width - 3, 2, 1, 1, 0xff000000);
		graphics.fillRect(width - 2, 3, 1, height - 6, 0xff000000);
		graphics.fillRect(1, height-4, 1, 1, 0xff000000);
		graphics.fillRect(2, height-3, 1, 1, 0xff000000);
		graphics.fillRect(3, height-2, width - 6, 1, 0xff000000);
		graphics.fillRect(width-3, height-3, 1, 1, 0xff000000);
	}

	public static void init() {}
}
