package io.github.astrarre.rendering.v0.api.textures;

import io.github.astrarre.stripper.Hide;
import io.github.astrarre.util.v0.api.Id;
import io.github.astrarre.util.v0.api.Validate;

import net.minecraft.util.Identifier;

/**
 * a path to an image, and it's size
 */
public class Texture {
	private final Identifier identifier;
	private final int offX, offY, width, height;

	public Texture(String modid, String path, int offX, int offY, int width, int height) {
		this(Id.newInstance(modid, path), width, height, offX, offY);
	}

	/**
	 * @param texture the path to the image
	 * @param width the width of the image
	 * @param height the height of the image
	 * @param offX the 'origin' of the texture (imagine this as like a cutout of an image)
	 */
	public Texture(Id texture, int offX, int offY, int width, int height) {
		this((Identifier) texture, offX, offY, width, height);
	}

	@Hide
	public Texture(Identifier texture, int offX, int offY, int width, int height) {
		this.identifier = texture;
		this.offX = offX;
		this.offY = offY;
		this.width = Validate.positive(width, "width");
		this.height = Validate.positive(height, "height");
	}

	public Texture(String modid, String path, int width, int height) {
		this(Id.newInstance(modid, path), width, height, 0, 0);
	}

	public Texture(Id texture, int width, int height) {
		this((Identifier) texture, 0, 0, width, height);
	}

	@Hide
	public Texture(Identifier texture, int width, int height) {
		this(texture, 0, 0, width, height);
	}

	public Id getId() {
		return (Id) this.identifier;
	}

	@Hide
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
