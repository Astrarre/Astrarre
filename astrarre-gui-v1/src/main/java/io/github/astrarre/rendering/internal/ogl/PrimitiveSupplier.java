package io.github.astrarre.rendering.internal.ogl;

import io.github.astrarre.rendering.v1.edge.shader.Global;
import io.github.astrarre.rendering.v1.edge.vertex.settings.VertexSetting;

public interface PrimitiveSupplier extends Global {
	PrimitiveImpl<?> create();
}
