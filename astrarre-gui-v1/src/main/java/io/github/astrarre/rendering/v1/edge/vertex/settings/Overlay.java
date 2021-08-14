package io.github.astrarre.rendering.v1.edge.vertex.settings;

import io.github.astrarre.rendering.internal.BufferSupplier;
import io.github.astrarre.rendering.v1.edge.vertex.VertexFormat;

import net.minecraft.client.render.VertexFormats;

public class Overlay<Next extends VertexSetting<?>> extends VertexSetting<Next> {
	static final Type<Overlay<?>> TYPE = type(Overlay::new, VertexFormats.OVERLAY_ELEMENT);

	public Overlay(BufferSupplier builder, VertexFormat<?> settings, VertexSetting next) {
		super(builder, settings, next);
	}


	public Next overlay(int u, int v) {
		this.builder().overlay(u, v);
		return this.next;
	}

	/**
	 * insert packed overlay uvs
	 */
	public Next overlay(int uv) {
		this.builder().overlay(uv);
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
