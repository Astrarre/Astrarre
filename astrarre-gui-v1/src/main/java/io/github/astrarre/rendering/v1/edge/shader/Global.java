package io.github.astrarre.rendering.v1.edge.shader;

import io.github.astrarre.rendering.v1.edge.Primitive;
import io.github.astrarre.rendering.v1.edge.mem.DataStack;

/**
 * @see Primitive
 * @see ShaderSetting
 */
public interface Global {
	DataStack getActive();
}
