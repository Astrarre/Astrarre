package io.github.astrarre.rendering.v1.edge;

import io.github.astrarre.rendering.v1.edge.shader.Global;
import io.github.astrarre.rendering.v1.edge.vertex.RenderLayer;

public interface VertexRenderer {
	<F extends Global> F render(RenderLayer<F> format);

	void flush();
	// todo add a cached shader settings thing
}
