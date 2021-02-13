package io.github.astrarre.gui.v0.api.cursor;

import io.github.astrarre.gui.v0.api.components.Component;
import io.github.astrarre.stripper.Hide;
import org.jetbrains.annotations.Nullable;

public class Cursor {
	private Object object;
	@Nullable
	private Component component;
	private float x, y;

	public void setObject(Object object, @Nullable Component renderer) {
		this.component = renderer;
		this.object = object;
	}

	/**
	 * @return the object the cursor is holding, File[], ItemStack, String, and Text are known types
	 */
	public Object getObject() {
		return this.object;
	}

	public float getX() {
		return this.x;
	}

	public float getY() {
		return this.y;
	}

	@Hide
	@Nullable
	public Component getComponent() {
		return this.component;
	}
}
