package io.github.astrarre.gui.v1.api.component.icon;

import io.github.astrarre.gui.v1.api.cursor.Cursor;
import io.github.astrarre.gui.v1.api.util.GuiRenderable;
import io.github.astrarre.rendering.v1.api.space.Render3d;
import io.github.astrarre.util.v0.api.SafeCloseable;

public interface Icon extends GuiRenderable {
	/**
	 * @return the height of the icon
	 */
	float height();

	/**
	 * @return the width of the icon
	 */
	float width();

	void render(Render3d render);

	@Override
	default void render(Cursor cursor, Render3d render) {
		this.render(render);
	}

	/**
	 * overlays the given icon ontop of the current one
	 */
	default Icon andThen(Icon icon) {
		return new Icon() {
			@Override
			public float height() {
				return Math.max(Icon.this.height(), icon.height());
			}

			@Override
			public float width() {
				return Math.max(Icon.this.width(), icon.width());
			}

			@Override
			public void render(Render3d render) {
				Icon.this.render(render);
				icon.render(render);
			}
		};
	}

	default Icon offset(float x, float y) {
		return new Icon() {
			@Override
			public float height() {
				return Icon.this.height() + y;
			}

			@Override
			public float width() {
				return Icon.this.width() + x;
			}

			@Override
			public void render(Render3d render) {
				try(SafeCloseable ignore = render.translate(x, y)) {
					Icon.this.render(render);
				}
			}
		};
	}
}
