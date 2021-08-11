package io.github.astrarre.rendering.v1.api.plane;

import io.github.astrarre.util.v0.api.Edge;
import io.github.astrarre.util.v0.api.Id;

import net.minecraft.client.texture.Sprite;

/**
 * a section of an image
 */
public record Texture(Id texture, float offX, float offY, float width, float height) {
	static Texture create(Id texture) {
		return new Texture(texture, 0, 0, 1, 1);
	}

	/**
	 * @param texture the id of the image
	 * @param imageWidth the width of the image
	 * @param offX the offset in the image to start the 'crop'
	 * @param width the size of the texture
	 */
	static Texture create(Id texture, int imageWidth, int imageHeight, int offX, int offY, int width, int height) {
		return new Texture(texture, offX / (float) imageWidth, offY / (float) imageHeight, width / (float) imageWidth, height / (float) imageHeight);
	}

	@Edge
	static Texture create(Sprite sprite) {
		return new Texture(Id.of(sprite.getAtlas().getId()), sprite.getMinU(), sprite.getMinV(), sprite.getMaxU(), sprite.getMaxV());
	}
	
	/**
	 * @return the id of the texture
	 */
	@Override
	public Id texture() {
		return this.texture;
	}

	/**
	 * @return a scalar value (0-1), where offX * width of image in pixels = offsetX in pixels
	 */
	@Override
	public float offX() {
		return this.offX;
	}

	/**
	 * @return a scalar value (0-1), where offY * width of image in pixels = offsetY in pixels
	 */
	@Override
	public float offY() {
		return this.offY;
	}

	/**
	 * @return a scalar value (0-1), where width * width of image in pixels = width in pixels
	 */
	@Override
	public float width() {
		return this.width;
	}

	/**
	 * @return a scalar value (0-1), where height * height of image in pixels = height in pixels
	 */
	@Override
	public float height() {
		return this.height;
	}

	public Texture crop(float width, float height) {
		return new Texture(this.texture, this.offX, this.offY, this.width * width, this.height * height);
	}

	public Texture crop(float offX, float offY, float width, float height) {
		return new Texture(this.texture, this.offX + offX * this.width, this.offY + offY * this.height, this.width * width, this.height * height);
	}
}
