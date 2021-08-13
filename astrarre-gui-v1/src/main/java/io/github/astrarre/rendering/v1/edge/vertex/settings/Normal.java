package io.github.astrarre.rendering.v1.edge.vertex.settings;

import io.github.astrarre.rendering.internal.BufferAccess;
import io.github.astrarre.rendering.internal.ogl.OpenGLRendererImpl;
import io.github.astrarre.rendering.v1.edge.vertex.VertexFormat;

import net.minecraft.client.render.VertexFormats;

public final class Normal<Next extends VertexSetting<?>> extends VertexSetting<Next> {
	static final Type<Normal<?>> TYPE = type(Normal::new, VertexFormats.NORMAL_ELEMENT);

	public Normal(BufferAccess builder, VertexFormat<?> settings, VertexSetting next) {
		super(builder, settings, next);
	}

	public Next normal(float x, float y, float z) {
		OpenGLRendererImpl impl = (OpenGLRendererImpl) this.builder;
		this.builder().normal(impl.stack.peek().getNormal(), x, y, z);
		return this.next;
	}
}
