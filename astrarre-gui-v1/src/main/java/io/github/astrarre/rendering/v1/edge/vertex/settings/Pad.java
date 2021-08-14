package io.github.astrarre.rendering.v1.edge.vertex.settings;

import io.github.astrarre.rendering.internal.BufferSupplier;
import io.github.astrarre.rendering.v1.edge.vertex.VertexFormat;

import net.minecraft.client.render.VertexFormats;

/**
 * @deprecated doesn't do anything
 * @param <Next>
 */
@Deprecated
public class Pad<Next extends VertexSetting<?>> extends VertexSetting<Next> {
	static final Type<Pad<?>> TYPE = type(Pad::new, VertexFormats.PADDING_ELEMENT);

	public Pad(BufferSupplier builder, VertexFormat<?> settings, VertexSetting next) {
		super(builder, settings, next);
	}


	public Next pad() {
		return this.next;
	}
}
