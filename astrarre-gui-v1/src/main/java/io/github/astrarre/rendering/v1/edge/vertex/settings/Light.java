package io.github.astrarre.rendering.v1.edge.vertex.settings;

import io.github.astrarre.rendering.internal.BufferSupplier;
import io.github.astrarre.rendering.v1.edge.shader.settings.LightTex;
import io.github.astrarre.rendering.v1.edge.vertex.RenderLayer;

import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexFormats;

/**
 * Position in a lightmap texture
 * @param <Next>
 */
public class Light<Next extends VertexSetting<?>> extends VertexSetting<Next> {
	static final Type<Light<?>> TYPE = type(Light::new, VertexFormats.LIGHT_ELEMENT);

	public Light(BufferSupplier builder, RenderLayer<?> settings, VertexSetting<?> next) {
		super(builder, settings, next);
	}

	/**
	 * @see LightTex#world()
	 */
	public Next light(int u, int v) {
		this.builder().light(u, v);
		return this.next;
	}

	/**
	 * insert packed overlay uvs
	 */
	public Next light(int uv) {
		this.builder().light(uv);
		return this.next;
	}

	/**
	 * full brightness uv coordinates if you're using {@link LightTex#world()}
	 */
	public Next emissive() {
		this.builder().light(LightmapTextureManager.MAX_LIGHT_COORDINATE); // 240, 240
		// the other one is 240, 0
		return this.next;
	}

	public static int packUV(int u, int v) {
		return ((v & 0xFFFF) << 16) | (u & 0xFFFF);
	}

	public static int unpackU(int uv) {
		return uv & 0xFFFF;
	}

	public static int unpackV(int uv) {
		return (uv >>> 16) & 0xFFFF;
	}
}
