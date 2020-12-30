package io.github.astrarre.gui.v0.api.textures;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import io.github.astrarre.common.v0.api.Validate;
import io.github.astrarre.gui.internal.textures.AstrarreSpriteManager;
import io.github.astrarre.stripper.Hide;
import io.github.astrarre.v0.client.texture.Sprite;
import io.github.astrarre.v0.util.Id;

import net.minecraft.client.MinecraftClient;
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

	static SpriteManager.Builder create(String modid, String path) {
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

	void forEach(Consumer<Sprite> consumer);

	@Hide
	Sprite getSprite(Identifier sprite);

	default Sprite getSprite(Id sprite) {
		return this.getSprite((Identifier) sprite);
	}

	class Builder {
		private final String modid;
		private final String path;
		private final List<Id> validIdentifiers = new ArrayList<>();

		public Builder(String modid, String path) {
			this.modid = modid;
			this.path = path;
		}

		@Hide
		public Builder add(Identifier identifier) {
			this.validIdentifiers.add((Id) identifier);
			return this;
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
