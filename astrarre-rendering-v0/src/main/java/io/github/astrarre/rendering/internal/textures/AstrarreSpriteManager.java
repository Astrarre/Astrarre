package io.github.astrarre.rendering.internal.textures;

import java.util.function.Supplier;
import java.util.stream.Stream;

import net.minecraft.client.texture.SpriteAtlasHolder;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;

public class AstrarreSpriteManager extends SpriteAtlasHolder {
	private final Supplier<Stream<Identifier>> sprites;
	public AstrarreSpriteManager(TextureManager textureManager, Identifier atlasId, String pathPrefix, Supplier<Stream<Identifier>> sprites) {
		super(textureManager, atlasId, pathPrefix);
		this.sprites = sprites;
	}

	@Override
	protected Stream<Identifier> getSprites() {
		return this.sprites.get();
	}
}
