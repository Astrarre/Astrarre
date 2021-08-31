package io.github.astrarre.rendering.v1.edge.vertex.settings;

import io.github.astrarre.rendering.internal.BufferSupplier;
import io.github.astrarre.rendering.internal.ogl.VertexRendererImpl;
import io.github.astrarre.rendering.v1.edge.vertex.RenderLayer;

import net.minecraft.client.render.VertexFormats;

public final class Normal<Next extends VertexSetting<?>> extends VertexSetting<Next> {
	static final Type<Normal<?>> TYPE = type(Normal::new, VertexFormats.NORMAL_ELEMENT);

	public Normal(BufferSupplier builder, RenderLayer<?> settings, VertexSetting<?> next) {
		super(builder, settings, next);
	}

	public Next normal(float x, float y, float z) {
		VertexRendererImpl impl = (VertexRendererImpl) this.builder;
		this.builder().normal(impl.stack.peek().getNormal(), x, y, z);
		return this.next;
	}
}
