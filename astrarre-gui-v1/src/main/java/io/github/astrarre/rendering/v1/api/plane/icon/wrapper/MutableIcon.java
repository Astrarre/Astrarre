package io.github.astrarre.rendering.v1.api.plane.icon.wrapper;

import io.github.astrarre.rendering.v1.api.plane.icon.Icon;
import io.github.astrarre.rendering.v1.api.space.Render3d;
import io.github.astrarre.util.v0.api.Validate;

public final class MutableIcon implements Icon {
	private Icon icon;

	public MutableIcon(Icon icon) {
		this.setIcon(icon);
	}

	public Icon getIcon() {
		return this.icon;
	}

	public MutableIcon setIcon(Icon icon) {
		Validate.notNull(icon, "icon cannot be null!");
		this.icon = icon;
		return this;
	}

	@Override
	public float width() {
		return this.icon.width();
	}

	@Override
	public float height() {
		return this.icon.height();
	}

	@Override
	public void render(Render3d render) {
		this.icon.render(render);
	}
}
