package io.github.astrarre.gui.v0.api.base.widgets.slider;

import io.github.astrarre.gui.v0.api.Drawable;
import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.gui.v0.api.access.Interactable;
import io.github.astrarre.networking.v0.api.io.Output;
import io.github.astrarre.rendering.v0.api.Graphics3d;

public class AbstractSlider extends Drawable implements Interactable {
	protected float progress;

	public AbstractSlider(DrawableRegistry.Entry id) {
		super(id);
	}

	@Override
	protected void render0(RootContainer container, Graphics3d graphics, float tickDelta) {

	}

	@Override
	protected void write0(RootContainer container, Output output) {

	}

	@Override
	public boolean isHovering(RootContainer container, double mouseX, double mouseY) {
		return true;
	}

	@Override
	public boolean mouseScrolled(RootContainer container, double mouseX, double mouseY, double amount) {
		System.out.println(amount);
		return true;
	}
}
