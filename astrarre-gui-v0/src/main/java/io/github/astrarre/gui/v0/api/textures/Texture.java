package io.github.astrarre.gui.v0.api.textures;

import io.github.astrarre.abstracted.v0.api.Validate;
import io.github.astrarre.stripper.Hide;
import io.github.astrarre.v0.util.Id;

import net.minecraft.util.Identifier;

/**
 * a path to an image, and it's size
 */
public class Texture {
	private final Identifier identifier;
	private final int width, height;


	public Texture(String modid, String path, int width, int height) {
		this(Id.newInstance(modid, path), width, height);
	}

	/**
	 * @param texture the path to the image
	 * @param width the width of the image
	 * @param height the height of the image
	 */
	public Texture(Id texture, int width, int height) {
		this((Identifier) texture, width, height);
	}

	@Hide
	public Texture(Identifier texture, int width, int height) {
		this.identifier = texture;
		this.width = Validate.positive(width, "width");
		this.height = Validate.positive(height, "height");
	}

	public Id getId() {
		return (Id) this.identifier;
	}

	public Identifier getIdentifier() {
		return this.identifier;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}
}
