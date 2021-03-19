package io.github.astrarre.gui.v0.api.base.panel;

import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.rendering.v0.api.Transformation;
import io.github.astrarre.rendering.v0.api.util.Polygon;
import io.github.astrarre.util.v0.api.Id;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * a panel who's [0, 0] lies at [centerX - width/2, centerY - height/2] of the root container
 */
public class ACenteringPanel extends APanel {
	private static final DrawableRegistry.Entry ENTRY = DrawableRegistry.register(Id.create("astrarre-gui-v0", "centering_panel"), ACenteringPanel::new);

	public final int width, height;

	public ACenteringPanel(int width, int height) {
		super(ENTRY);
		this.width = width;
		this.height = height;
		this.setBounds(Polygon.rectangle(width, height));
	}

	@Environment(EnvType.CLIENT)
	private ACenteringPanel(NBTagView input) {
		super(ENTRY, input);
		this.width = input.getInt("width");
		this.height = input.getInt("height");
	}

	@Override
	protected void onAdded(RootContainer container) {
		super.onAdded(container);
		container.addResizeListener((width, height) -> this.setTransformation(Transformation.translate(width/2f - this.width/2f, height/2f - this.height/2f, 0)));
	}

	@Override
	public void write0(RootContainer container, NBTagView.Builder output) {
		super.write0(container, output);
		output.putInt("width", this.width);
		output.putInt("height", this.height);
	}

	public static void init() {}
}
