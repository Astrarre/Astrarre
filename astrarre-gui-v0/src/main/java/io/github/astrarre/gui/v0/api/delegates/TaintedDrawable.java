package io.github.astrarre.gui.v0.api.delegates;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.astrarre.gui.v0.api.Drawable;
import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.gui.v0.api.base.DelegateDrawable;
import io.github.astrarre.networking.v0.api.io.Input;
import io.github.astrarre.networking.v0.api.io.Output;
import io.github.astrarre.rendering.internal.MatrixGraphics;
import io.github.astrarre.rendering.v0.api.Graphics3d;
import io.github.astrarre.rendering.v0.api.graphics.DelegateGraphics;
import io.github.astrarre.util.v0.api.Id;
import io.github.astrarre.util.v0.api.Validate;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * change the color of a drawable
 */
public final class TaintedDrawable extends DelegateDrawable {
	private static final DrawableRegistry.Entry ENTRY = DrawableRegistry.register(Id.create("astrarre-gui-v0", "tainted_drawable"), TaintedDrawable::new);
	protected final float red, green, blue, alpha;

	public TaintedDrawable(Drawable drawable, float red, float green, float blue, float alpha) {
		this(ENTRY, drawable, red, green, blue, alpha);
	}

	protected TaintedDrawable(DrawableRegistry.Entry id, Drawable drawable, float red, float green, float blue, float alpha) {
		super(id, drawable);
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
	}

	@Environment(EnvType.CLIENT)
	private TaintedDrawable(Input input) {
		this(ENTRY, input);
	}

	@Environment(EnvType.CLIENT)
	protected TaintedDrawable(DrawableRegistry.Entry id, Input input) {
		super(id, input);
		this.red = input.readFloat();
		this.green = input.readFloat();
		this.blue = input.readFloat();
		this.alpha = input.readFloat();
	}

	@Override
	protected void render0(RootContainer container, Graphics3d graphics, float tickDelta) {
		Validate.isTrue(DelegateGraphics.resolve(graphics) instanceof MatrixGraphics, "TaintedDrawable only works with matrix graphics!");
		RenderSystem.pushMatrix();
		RenderSystem.color4f(this.red, this.green, this.blue, this.alpha);
		this.getDelegate().render(container, graphics, tickDelta);
		RenderSystem.color4f(1, 1, 1, 1);
		RenderSystem.popMatrix();
	}

	@Override
	protected void write0(RootContainer container, Output output) {
		super.write0(container, output);
		output.writeFloat(this.red);
		output.writeFloat(this.green);
		output.writeFloat(this.blue);
		output.writeFloat(this.alpha);
	}

	public static void init() {}
}
