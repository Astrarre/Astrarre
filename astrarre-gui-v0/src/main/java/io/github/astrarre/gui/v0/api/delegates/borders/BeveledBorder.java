package io.github.astrarre.gui.v0.api.delegates.borders;

import io.github.astrarre.gui.v0.api.Drawable;
import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.gui.v0.api.DelegateDrawable;
import io.github.astrarre.gui.v0.api.statik.BeveledRectangle;
import io.github.astrarre.networking.v0.api.io.Input;
import io.github.astrarre.rendering.v0.api.Graphics3d;
import io.github.astrarre.rendering.v0.api.Transformation;
import io.github.astrarre.rendering.v0.api.util.Close;
import io.github.astrarre.rendering.v0.api.util.Polygon;
import io.github.astrarre.util.v0.api.Id;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public final class BeveledBorder extends DelegateDrawable {
	public static final Transformation TRANSLATE_N2_N2_0 = Transformation.translate(-4, -4, 0);
	private static final DrawableRegistry.Entry ENTRY = DrawableRegistry.register(Id.create("astrarre-gui-v0", "beveled_border"),
			BeveledBorder::new);

	public BeveledBorder(Drawable delegate) {
		super(ENTRY, delegate);
	}

	@Environment (EnvType.CLIENT)
	private BeveledBorder(Input input) {
		super(ENTRY, input);
	}

	@Override
	protected void render0(RootContainer container, Graphics3d graphics, float tickDelta) {
		Polygon polygon = this.getDelegate().getBounds().getEnclosing();
		Close close = graphics.applyTransformation(TRANSLATE_N2_N2_0);
		BeveledRectangle.drawBevel(graphics, polygon.getX(2) + 8, polygon.getY(2) + 8);
		close.close();
		super.render0(container, graphics, tickDelta);
	}

	public static void init() {}
}
