package io.github.astrarre.rendering.v1.api.plane.icon.wrapper;

import io.github.astrarre.gui.v1.api.component.APanel;
import io.github.astrarre.rendering.v1.api.plane.Transform2d;
import io.github.astrarre.rendering.v1.api.plane.icon.Icon;
import io.github.astrarre.rendering.v1.api.space.Render3d;

public record RealignedIcon(Icon icon, Transform2d transform) implements Icon {
	@Override
	public float width() {
		return this.dim(Transform2d::transformX, true);
	}

	@Override
	public float height() {
		return this.dim(Transform2d::transformY, true);
	}

	@Override
	public void render(Render3d render) {
		try(var ignore1 = render.translate(-this.dim(Transform2d::transformX, false), -this.dim(Transform2d::transformY, false))) {
			try(var ignore = render.transform(this.transform)) {
				this.icon.render(render);
			}
		}
	}

	float dim(APanel.CoordinateTransformer t, boolean size) {
		float w = this.icon.width(), h = this.icon.height();
		float a = t.accept(this.transform, w, h);
		float b = t.accept(this.transform, 0, h);
		float c = t.accept(this.transform, w, 0);
		float d = t.accept(this.transform, 0, 0);

		float min = Math.min(Math.min(a, b), Math.min(c, d));
		if(size) {
			float max = Math.max(Math.max(a, b), Math.max(c, d));
			return max - min;
		} else {
			return min;
		}
	}
}
