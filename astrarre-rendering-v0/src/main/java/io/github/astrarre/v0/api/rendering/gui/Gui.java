package io.github.astrarre.v0.api.rendering.gui;

import io.github.astrarre.v0.api.rendering.gui.texture.Texture;

public class Gui {
	private Texture background;
	private int width, height;

	public Gui setDimensions(int width, int height) {
		this.width = width;
		this.height = height;
		return this;
	}
}
