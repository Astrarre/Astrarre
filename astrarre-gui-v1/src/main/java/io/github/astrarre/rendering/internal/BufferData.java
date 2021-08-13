package io.github.astrarre.rendering.internal;

import java.util.Map;

import io.github.astrarre.rendering.v1.edge.shader.Global;
import io.github.astrarre.rendering.v1.edge.vertex.VertexFormat;

public interface BufferData {
	Map<VertexFormat<?>, Global> astrarre_configCache();


}
