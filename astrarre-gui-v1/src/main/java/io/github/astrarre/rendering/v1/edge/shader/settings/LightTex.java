package io.github.astrarre.rendering.v1.edge.shader.settings;

import io.github.astrarre.rendering.v1.edge.shader.Global;

import net.minecraft.client.MinecraftClient;

/**
 * Configures the light map texture
 */
public class LightTex<T extends Global> extends Img<T> {
	static final Factory<LightTex<?>> FACTORY = val -> new LightTex<>(val, 2);

	public LightTex(T val, int index) {
		super(val, index);
	}

	/**
	 * The client world lightmap, it is affected by time of day and things like that. This is what minecraft uses for everything.
	 * Here's an example image:
	 * <br>
	 * <img src="{@docRoot}/doc-files/texture_map.png">
	 */
	public T world() {
		MinecraftClient client = MinecraftClient.getInstance();
		this.getActive().push(client.gameRenderer.getLightmapTextureManager()).setSetting(this);
		return this.getNext();
	}
}
