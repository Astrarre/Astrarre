package io.github.astrarre.gui.v0.api.base.borders;

import io.github.astrarre.gui.v0.api.ADelegateDrawable;
import io.github.astrarre.gui.v0.api.ADrawable;
import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.gui.v0.api.base.statik.ABeveledRectangle;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.gui.v0.api.graphics.GuiGraphics;
import io.github.astrarre.rendering.v0.api.Transformation;
import io.github.astrarre.rendering.v0.api.util.Close;
import io.github.astrarre.rendering.v0.api.util.Polygon;
import io.github.astrarre.util.v0.api.Id;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public final class ABeveledBorder extends ADelegateDrawable {
	public static final Transformation TRANSLATE_N2_N2_0 = Transformation.translate(-5, -4, 0);
	private static final DrawableRegistry.Entry ENTRY = DrawableRegistry.register(Id.create("astrarre-gui-v0", "beveled_border"),
			ABeveledBorder::new);

	public ABeveledBorder(ADrawable delegate) {
		super(ENTRY, delegate);
	}

	@Environment (EnvType.CLIENT)
	private ABeveledBorder(NBTagView input) {
		super(ENTRY, input);
	}

	@Override
	protected void render0(RootContainer container, GuiGraphics graphics, float tickDelta) {
		Polygon polygon = this.getDelegate().getBounds().getEnclosing();
		Close close = graphics.applyTransformation(TRANSLATE_N2_N2_0);
		ABeveledRectangle.drawBevel(graphics, polygon.getX(2) + 10, polygon.getY(2) + 9);
		close.close();
		super.render0(container, graphics, tickDelta);
	}

	public static void init() {}
}
