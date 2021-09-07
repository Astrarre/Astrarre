package io.github.astrarre.rendering.v1.api.plane.icon.wrapper;

import io.github.astrarre.gui.v1.api.component.APanel;
import io.github.astrarre.rendering.v1.api.plane.Transform2d;
import io.github.astrarre.rendering.v1.api.plane.icon.Icon;
import io.github.astrarre.rendering.v1.api.space.Render3d;

public record TransformedIcon(Icon icon, Transform2d transform) implements Icon {
	@Override
	public float width() {
		return APanel.bounds(this.transform, this.icon.width(), this.icon.height()).maxX();
	}

	@Override
	public float height() {
		return APanel.bounds(this.transform, this.icon.width(), this.icon.height()).maxY();
	}

	@Override
	public void render(Render3d render) {
		try(var ignore = render.transform(this.transform)) {
			this.icon.render(render);
		}
	}
}
