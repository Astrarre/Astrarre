package io.github.astrarre.rendering.v1.api.plane.icon.wrapper;

import io.github.astrarre.rendering.v1.api.plane.icon.Icon;
import io.github.astrarre.rendering.v1.api.space.Render3d;
import io.github.astrarre.util.v0.api.SafeCloseable;

public record OffsetIcon(Icon icon, float offX, float offY) implements Icon {

	@Override
	public float width() {
		return this.icon.width() + this.offX;
	}

	@Override
	public float height() {
		return this.icon.height() + this.offY;
	}

	@Override
	public void render(Render3d render) {
		try(SafeCloseable ignore = render.translate(this.offX, this.offY)) {
			this.icon.render(render);
		}
	}
}
