package io.github.astrarre.gui.v0.api.base.borders;

import io.github.astrarre.gui.v0.api.ADelegateDrawable;
import io.github.astrarre.gui.v0.api.ADrawable;
import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.gui.v0.api.graphics.GuiGraphics;
import io.github.astrarre.util.v0.api.Id;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * makes a black line around the bounds of the delegate drawable
 */
public final class ASimpleBorder extends ADelegateDrawable {
	private static final DrawableRegistry.Entry ENTRY = DrawableRegistry.register(Id.create("astrarre-gui-v0", "simple_border"), ASimpleBorder::new);
	public ASimpleBorder(ADrawable delegate) {
		super(ENTRY, delegate);
	}

	@Environment(EnvType.CLIENT)
	protected ASimpleBorder(NBTagView input) {
		super(ENTRY, input);
	}

	@Override
	protected void render0(RootContainer container, GuiGraphics graphics, float tickDelta) {
		super.render0(container, graphics, tickDelta);
		this.getBounds().walk((x1, y1, x2, y2) -> graphics.drawLine(x1, y1, x2, y2, 0xff000000));
	}

	public static void init() {}
}
