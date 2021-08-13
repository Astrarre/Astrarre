package io.github.astrarre.rendering.v1.edge;

import io.github.astrarre.rendering.v1.edge.shader.Global;
import io.github.astrarre.rendering.v1.edge.vertex.settings.VertexSetting;
import io.github.astrarre.util.v0.api.SafeCloseable;

public interface Primitive<V extends VertexSetting<?>> extends Global, SafeCloseable {
	V quad();

	V line();

	V continuousLine();

	V triangle();

	/**
	 * releases any resources to pools, the instance is now invalid and any future calls to it are undefined behavior
	 */
	@Override
	void close();
}
