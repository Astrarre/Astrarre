package io.github.astrarre.rendering.v1.edge.vertex.settings;

import io.github.astrarre.rendering.internal.BufferSupplier;
import io.github.astrarre.rendering.internal.ogl.VertexRendererImpl;
import io.github.astrarre.rendering.v1.edge.vertex.VertexFormat;

import net.minecraft.client.render.VertexFormats;

public final class Pos<Next extends VertexSetting> extends VertexSetting<Next> {
	static final Type<Pos<?>> TYPE = type(Pos::new, VertexFormats.POSITION_ELEMENT);

	public Pos(BufferSupplier builder, VertexFormat<?> settings, VertexSetting<?> next) {
		super(builder, settings, next);
	}

	public Next pos(float x, float y, float z) {
		VertexRendererImpl impl = (VertexRendererImpl) this.builder;
		this.builder().vertex(impl.stack.peek().getModel(), x, y, z);
		return this.next;
	}
}
