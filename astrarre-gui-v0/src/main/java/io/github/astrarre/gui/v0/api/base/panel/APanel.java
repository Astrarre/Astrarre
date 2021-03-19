package io.github.astrarre.gui.v0.api.base.panel;

import io.github.astrarre.gui.v0.api.Drawable;
import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.gui.v0.api.base.AggregateDrawable;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.rendering.v0.api.Graphics3d;
import io.github.astrarre.util.v0.api.Id;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class APanel extends AggregateDrawable {
	private static final DrawableRegistry.Entry PANEL = DrawableRegistry.register(Id.create("astrarre-gui-v0", "panel"), APanel::new);

	public APanel() {
		this(PANEL);
	}

	protected APanel(DrawableRegistry.Entry entry) {
		super(entry);
	}

	@Environment (EnvType.CLIENT)
	private APanel(NBTagView input) {
		this(PANEL, input);
	}

	@Environment (EnvType.CLIENT)
	protected APanel(DrawableRegistry.Entry entry, NBTagView input) {
		super(entry, input);
	}

	public static void init() {}

	@Override
	protected void render0(RootContainer container, Graphics3d graphics, float tickDelta) {
		for (Drawable drawable : this.drawables) {
			drawable.render(container, graphics, tickDelta);
		}
	}
}
