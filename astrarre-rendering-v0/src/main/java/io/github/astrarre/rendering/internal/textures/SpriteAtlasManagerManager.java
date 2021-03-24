package io.github.astrarre.rendering.internal.textures;

import java.util.HashMap;
import java.util.Map;

import io.github.astrarre.rendering.internal.mixin.BakedModelManagerAccess;
import io.github.astrarre.rendering.internal.mixin.SpriteAtlasManagerAccess;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.SpriteAtlasManager;
import net.minecraft.util.Identifier;

public class SpriteAtlasManagerManager {
	public static final Map<Identifier, AstrarreSpriteManager> MANAGER = new HashMap<>();
	private static final MinecraftClient CLIENT = MinecraftClient.getInstance();
	public static SpriteAtlasManager getManager() {
		return ((BakedModelManagerAccess)CLIENT.getBakedModelManager()).getAtlasManager();
	}

	public static void register(AstrarreSpriteManager manager) {
		MANAGER.put(manager.atlasId, manager);
		((SpriteAtlasManagerAccess)getManager()).getAtlases().put(manager.atlasId, manager.getTexture());
	}
}
