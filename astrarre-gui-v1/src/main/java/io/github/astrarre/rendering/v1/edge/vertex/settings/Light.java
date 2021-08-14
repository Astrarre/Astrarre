package io.github.astrarre.rendering.v1.edge.vertex.settings;

import io.github.astrarre.rendering.internal.BufferSupplier;
import io.github.astrarre.rendering.v1.edge.vertex.VertexFormat;

import net.minecraft.client.render.VertexFormats;

public class Light<Next extends VertexSetting<?>> extends VertexSetting<Next> {
	static final Type<Light<?>> TYPE = type(Light::new, VertexFormats.LIGHT_ELEMENT);

	public Light(BufferSupplier builder, VertexFormat<?> settings, VertexSetting next) {
		super(builder, settings, next);
	}


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
