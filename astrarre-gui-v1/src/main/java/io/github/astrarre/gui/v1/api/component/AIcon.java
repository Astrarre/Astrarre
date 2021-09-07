package io.github.astrarre.gui.v1.api.component;

import java.util.function.Supplier;

import io.github.astrarre.gui.v1.api.listener.cursor.Cursor;
import io.github.astrarre.rendering.v1.api.plane.icon.Icon;
import io.github.astrarre.rendering.v1.api.space.Render3d;

/**
 * Displays an icon in component form, can have tooltips added to it
 * @see Icon
 */
public class AIcon extends AHoverableComponent {
	private Supplier<Icon> icon;

	public AIcon(Icon icon) {
		this.icon = () -> icon;
	}

	public AIcon(Supplier<Icon> icon) {
		this.icon = icon;
	}

	@Override
	protected void render0(Cursor cursor, Render3d render) {
		if(this.icon != null) {
			this.icon.get().render(render);
		}
	}

	public Icon getIcon() {
		return this.icon.get();
	}

	public AIcon setIcon(Icon icon) {
		this.icon = () -> icon;
		return this;
	}

	public AIcon setIcon(Supplier<Icon> icon) {
		this.icon = icon;
		return this;
	}

	@Override
	public float getWidth() {
		return this.icon.get().width();
	}

	@Override
	public float getHeight() {
		return this.icon.get().height();
	}
}
