package io.github.astrarre.rendering.internal;

import io.github.astrarre.rendering.v1.edge.vertex.VertexFormat;
import io.github.astrarre.rendering.v1.edge.vertex.settings.VertexSetting;

import net.minecraft.client.render.BufferBuilder;

public interface BufferAccess {
	BufferBuilder getBuffer(VertexSetting<?> setting, VertexFormat<?> settings);
}
