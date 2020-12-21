package io.github.astrarre.v0.api.rendering.gui.texture;

import io.github.astrarre.v0.api.util.Validate;
import io.github.astrarre.v0.util.Id;

/**
 * a path to an image, and it's size
 */
public class Texture {
	private final Id identifier;
	private final int width, height;

	public Texture create(Id identifier, int width, int height) {
		return new Texture(identifier, width, height);
	}

	public Texture(String modid, String path, int width, int height) {
		this(Id.newInstance(modid, path), width, height);
	}

	public Texture(Id identifier, int width, int height) {
		this.identifier = identifier;
		this.width = Validate.positive(width, "width");
		this.height = Validate.positive(height, "height");
	}

	public Id getIdentifier() {
		return this.identifier;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}
}
