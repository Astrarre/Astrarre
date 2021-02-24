package io.github.astrarre.rendering.v0.api;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;


import io.github.astrarre.rendering.internal.textures.AstrarreSpriteManager;
import io.github.astrarre.rendering.v0.api.textures.SpriteInfo;
import io.github.astrarre.util.v0.api.Id;
import io.github.astrarre.util.v0.api.Validate;
import io.github.astrarre.v0.client.texture.Sprite;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.util.Identifier;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment (EnvType.CLIENT)
public interface SpriteManager {
	SpriteManager POTION_EFFECTS = Validate.instanceOf(MinecraftClient.getInstance().getStatusEffectSpriteManager(),
			SpriteManager.class,
			"SpriteManager was loaded too early!");
	SpriteManager PAINTINGS = Validate.instanceOf(MinecraftClient.getInstance().getPaintingManager(),
			SpriteManager.class,
			"SpriteManager was loaded too early!");
	SpriteManager BLOCKS = Validate.instanceOf(MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE),
			SpriteManager.class,
			"SpriteManager was loaded too early!");
	SpriteManager PARTICLES = Validate.instanceOf(MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.PARTICLE_ATLAS_TEXTURE),
			SpriteManager.class,
			"SpriteManager was loaded too early!");

	static Builder create(String modid, String path) {
		return new Builder(modid, path);
	}

	/**
	 * this must be created in a client initializer
	 */
	static SpriteManager create(String modid, String path, Supplier<Stream<Id>> valid) {
		AstrarreSpriteManager manager = new AstrarreSpriteManager(MinecraftClient.getInstance().getTextureManager(),
				new Identifier(modid, "textures/atlas/" + path + "s.png"),
				path,
				(Supplier) valid);
		((ReloadableResourceManager) MinecraftClient.getInstance().getResourceManager()).registerListener(manager);
		return (SpriteManager) manager;
	}

	void forEach(Consumer<SpriteInfo> consumer);

	SpriteInfo getSprite(Id sprite);

	class Builder {
		private final String modid;
		private final String path;
		private final List<Id> validIdentifiers = new ArrayList<>();

		public Builder(String modid, String path) {
			this.modid = modid;
			this.path = path;
		}

		public Builder add(Id id) {
			this.validIdentifiers.add(id);
			return this;
		}

		public String getModid() {
			return this.modid;
		}

		public String getPath() {
			return this.path;
		}

		public SpriteManager build() {
			List<Id> ids = new ArrayList<>(this.validIdentifiers);
			return create(this.modid, this.path, ids::stream);
		}
	}
}
