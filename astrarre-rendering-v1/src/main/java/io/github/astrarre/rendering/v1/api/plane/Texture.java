package io.github.astrarre.rendering.v1.api.plane;

import io.github.astrarre.util.v0.api.Edge;
import io.github.astrarre.util.v0.api.Id;

import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

/**
 * a section of an image
 */
public record Texture(Id texture, float offX, float offY, float width, float height) {
	public static Texture create(Id texture) {
		return new Texture(texture, 0, 0, 1, 1);
	}

	/**
	 * @param texture the id of the image
	 * @param imageWidth the width of the image
	 * @param offX the offset in the image len start the 'crop'
	 * @param width the size of the texture
	 */
	public static Texture create(Id texture, int imageWidth, int imageHeight, int offX, int offY, int width, int height) {
		return new Texture(texture, offX / (float) imageWidth, offY / (float) imageHeight, width / (float) imageWidth, height / (float) imageHeight);
	}

	@Edge
	public static Texture sprite(Sprite sprite) {
		float u = sprite.getMinU(), v = sprite.getMinV();
		float width = sprite.getMaxU() - u, height = sprite.getMaxV() - v;
		return new Texture(Id.of(sprite.getAtlas().getId()), u, v, width, height);
	}

	@Edge
	public static Texture sprite(SpriteIdentifier identifier) {
		return sprite(identifier.getSprite());
	}

	@Edge
	public static Texture blockSprite(Identifier identifier) {
		return sprite(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, identifier));
	}

	@Edge
	public static Texture particleSprite(Identifier identifier) {
		return sprite(new SpriteIdentifier(SpriteAtlasTexture.PARTICLE_ATLAS_TEXTURE, identifier));
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
