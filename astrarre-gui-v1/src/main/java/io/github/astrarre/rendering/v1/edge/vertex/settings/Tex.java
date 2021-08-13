package io.github.astrarre.rendering.v1.edge.vertex.settings;

import io.github.astrarre.rendering.internal.BufferAccess;
import io.github.astrarre.rendering.v1.edge.shader.ShaderSetting;
import io.github.astrarre.rendering.v1.edge.vertex.VertexFormat;

import net.minecraft.client.render.VertexFormats;

public final class Tex<Next extends VertexSetting> extends VertexSetting<Next> {
	public static final Type<Tex<?>> TYPE = type(Tex::new, VertexFormats.TEXTURE_ELEMENT);

	public Tex(BufferAccess builder, VertexFormat<?> settings, VertexSetting next) {
		super(builder, settings, next);
	}


	public Next tex(float u, float v) {
		this.builder().texture(u, v);
		return this.next;
	}
}
