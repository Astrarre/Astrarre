package io.github.astrarre.gui.v0.api.panel;

import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.networking.v0.api.io.Input;
import io.github.astrarre.networking.v0.api.io.Output;
import io.github.astrarre.rendering.v0.api.Transformation;
import io.github.astrarre.rendering.v0.api.util.Polygon;
import io.github.astrarre.util.v0.api.Id;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * a panel who's [0, 0] lies at [centerX - width/2, centerY - height/2] of the root container
 */
public class CenteringPanel extends Panel {
	private static final DrawableRegistry.Entry ENTRY = DrawableRegistry.register(Id.create("astrarre-gui-v0", "centering_panel"), CenteringPanel::new);

	@Environment(EnvType.CLIENT)
	protected Transformation original;
	public final int width, height;

	public CenteringPanel(int width, int height) {
		super(ENTRY);
		this.width = width;
		this.height = height;
		this.setBoundsProtected(Polygon.rectangle(width, height));
	}

	@Environment(EnvType.CLIENT)
	private CenteringPanel(Input input) {
		super(ENTRY, input);
		this.original = this.getTransformation();
		this.width = input.readInt();
		this.height = input.readInt();
	}

	@Override
	protected void onAdded(RootContainer container) {
		super.onAdded(container);
		container.addResizeListener((width, height) -> this.setTransformationProtected(Transformation.translate(width/2f - this.width/2f, height/2f - this.height/2f, 0).combine(this.original)));
	}

	@Override
	@Environment(EnvType.CLIENT)
	public CenteringPanel setTransformation(Transformation transformation) {
		this.original = transformation;
		super.setTransformationProtected(transformation);
		return this;
	}

	@Override
	public void write0(RootContainer container, Output output) {
		super.write0(container, output);
		output.writeInt(this.width);
		output.writeInt(this.height);
	}

	@Override
	public void setBounds(Polygon polygon) {
		throw new UnsupportedOperationException();
	}

	public static void init() {}
}
