package io.github.astrarre.rendering.internal;

import io.github.astrarre.rendering.v1.edge.vertex.RenderLayer;
import io.github.astrarre.rendering.v1.edge.vertex.settings.VertexSetting;

import net.minecraft.client.render.VertexConsumer;

public interface BufferSupplier {
	VertexConsumer getBuffer(VertexSetting<?> setting, RenderLayer<?> settings);
}
