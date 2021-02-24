package io.github.astrarre.gui.v0.api;

import io.github.astrarre.rendering.v0.api.Graphics3d;

public abstract class Drawable {
	private int id = Integer.MIN_VALUE;

	public abstract void render(Graphics3d graphics);

	final void initId(int id) {
		if (id == Integer.MIN_VALUE) {
			this.id = id;
		} else {
			throw new IllegalArgumentException("cannot register the same drawable more than once!");
		}
	}

	final void remove0() {
		this.id = Integer.MIN_VALUE;
		this.remove();
	}

	public void remove() {}
}
