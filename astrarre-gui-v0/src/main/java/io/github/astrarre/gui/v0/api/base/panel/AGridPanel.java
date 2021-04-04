package io.github.astrarre.gui.v0.api.base.panel;

import io.github.astrarre.gui.v0.api.ADrawable;
import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.rendering.v0.api.Transformation;
import io.github.astrarre.rendering.v0.api.util.Polygon;
import io.github.astrarre.util.v0.api.Id;

public class AGridPanel extends APanel {
	private static final DrawableRegistry.Entry ENTRY = DrawableRegistry.registerForward(Id.create("astrarre-gui-v0", "grid_panel"),
			AGridPanel::new);
	public final float width, height;
	public final int gridWidth, gridHeight;

	/**
	 * @param width the width of the entire panel
	 * @param gridWidth the number of squares across the grid
	 */
	public AGridPanel(float width, float height, int gridWidth, int gridHeight) {
		this(ENTRY, width, height, gridWidth, gridHeight);
	}

	protected AGridPanel(DrawableRegistry.Entry entry, float width, float height, int gridWidth, int gridHeight) {
		super(entry);
		this.setBounds(Polygon.rectangle(width, height));
		this.width = width;
		this.height = height;
		this.gridWidth = gridWidth;
		this.gridHeight = gridHeight;
	}

	protected AGridPanel(DrawableRegistry.Entry entry, NBTagView view) {
		super(entry);
		this.width = view.getFloat("width");
		this.height = view.getFloat("height");
		this.gridWidth = view.getInt("gridWidth");
		this.gridHeight = view.getInt("gridHeight");
	}

	/**
	 * adds and aligns the drawable (by overriding it's Transformation) to the specified grid
	 */
	public void add(ADrawable drawable, int gridX, int gridY) {
		this.add(drawable);
		drawable.setTransformation(Transformation.translate(this.getCellWidth() * gridX, this.getCellHeight() * gridY, 0));
	}

	public float getCellWidth() {
		return this.width / this.gridWidth;
	}

	public float getCellHeight() {
		return this.height / this.gridHeight;
	}

	public static void init() {}

	@Override
	protected void write0(RootContainer container, NBTagView.Builder output) {
		super.write0(container, output);
		output.putFloat("width", this.width).putFloat("height", this.height).putInt("gridWidth", this.gridWidth)
				.putInt("gridHeight", this.gridHeight);
	}
}
