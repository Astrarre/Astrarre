package io.github.astrarre.gui.v1.api.component.icon;

import io.github.astrarre.rendering.v1.api.space.Render3d;
import io.github.astrarre.util.v0.api.SafeCloseable;

public record ScaledIcon(Icon icon, float scaleX, float scaleY) implements Icon {

	@Override
	public float height() {
		return this.icon.height() * this.scaleY;
	}

	@Override
	public float width() {
		return this.icon.width() * this.scaleX;
	}

	@Override
	public void render(Render3d render) {
		try(SafeCloseable ignore = render.scale(this.scaleX, this.scaleY)) {
			this.icon.render(render);
		}
	}

	// optimization, prevents holding onto icons
	@Override
	public Icon scale(float scaleX, float scaleY) {
		return new ScaledIcon(this.icon, this.scaleX * scaleX, this.scaleY * scaleX);
	}
}
