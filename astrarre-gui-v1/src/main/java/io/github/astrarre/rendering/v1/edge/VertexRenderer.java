package io.github.astrarre.rendering.v1.edge;

import io.github.astrarre.rendering.v1.edge.shader.Global;
import io.github.astrarre.rendering.v1.edge.vertex.VertexFormat;

public interface VertexRenderer {
	<F extends Global> F render(VertexFormat<F> format);

	void flush();
	// todo add a cached shader settings thing
}
