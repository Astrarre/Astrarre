package io.github.astrarre.rendering.v0.api.textures;

public class TexturePart {
	public final Texture texture;
	public final int offX, offY, width, height;

	public TexturePart(Texture texture, int offX, int offY, int width, int height) {
		this.texture = texture;
		this.offX = offX;
		this.offY = offY;
		this.width = width;
		this.height = height;
	}

	public TexturePart(String modid, String name, int totalWidth, int totalHeight, int offX, int offY, int width, int height) {
		this(new Texture(modid, name, totalWidth, totalHeight), offX, offY, width, height);
	}
}
