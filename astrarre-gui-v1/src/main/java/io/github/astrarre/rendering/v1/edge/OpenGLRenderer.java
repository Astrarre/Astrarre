package io.github.astrarre.rendering.v1.edge;

import io.github.astrarre.rendering.v1.edge.shader.Global;
import io.github.astrarre.rendering.v1.edge.shader.ShaderSetting;
import io.github.astrarre.rendering.v1.edge.vertex.VertexFormat;
import io.github.astrarre.rendering.v1.edge.vertex.settings.VertexSetting;

public interface OpenGLRenderer {
	<F extends Global> F render(VertexFormat<F> format);

	// todo add a cached shader settings thing
}
