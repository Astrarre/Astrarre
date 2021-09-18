package io.github.astrarre.gui.v1.api.component;

import io.github.astrarre.rendering.v1.api.plane.Transform2d;
import io.github.astrarre.rendering.v1.api.space.Transform3d;

/**
 * A panel that stays in the middle of it's parent
 */
public class ACenteringPanel extends APanel {
	final AComponent component;
	Transform3d transform = Transform3d.IDENTITY;
	float offX, offY;

	public ACenteringPanel(AComponent parent) {
		this.component = parent;
		this.onResize.andThen((w, h) -> this.recomputeOffset());
		parent.onResize.andThen((w, h) -> this.recomputeOffset());
	}

	protected void recomputeOffset() {
		this.offX = (this.component.getWidth() - super.getWidth()) / 2;
		this.offY = (this.component.getHeight() - super.getHeight()) / 2;
		this.transform = Transform2d.translate(this.offX, this.offY);
	}

	@Override
	Transform3d transform0() {
		return this.transform;
	}
}
