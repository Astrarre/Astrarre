package io.github.astrarre.rendering.v0.api.textures.client;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import io.github.astrarre.rendering.internal.textures.AstrarreSpriteManager;
import io.github.astrarre.rendering.internal.textures.SpriteAtlasManagerManager;
import io.github.astrarre.util.v0.api.Id;

import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.util.Identifier;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * make your own sprite atlas
 */
@Environment (EnvType.CLIENT)
public interface SpriteManager {
	static Builder create(String modid, String path) {
		return new Builder(modid, path);
	}

	/**
	 * this must be created in a client initializer
	 */
	@SuppressWarnings ({
			"ConstantConditions",
			"unchecked",
			"rawtypes"
	})
	static SpriteManager create(String modid, String path, Supplier<Stream<Id>> valid) {
		AstrarreSpriteManager manager = new AstrarreSpriteManager(MinecraftClient.getInstance().getTextureManager(),
				new Identifier(modid, "textures/atlas/" + path + "s.png"),
				path,
				(Supplier) valid);
		MinecraftClient client = MinecraftClient.getInstance();
		((ReloadableResourceManager) client.getResourceManager()).registerReloader(manager);
		SpriteAtlasManagerManager.register(manager);
		return (SpriteManager) manager;
	}

	void forEach(Consumer<ManagedSprite> consumer);

	ManagedSprite getSprite(Id sprite);

	@Environment (EnvType.CLIENT)
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
