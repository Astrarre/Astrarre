package io.github.astrarre.rendering.v1.edge.glslTransformer.inject;

import java.util.List;

import io.github.astrarre.rendering.v1.edge.glslTransformer.ReplaceToken;
import io.github.astrarre.rendering.v1.edge.glslTransformer.Token;

public interface Transform {
	List<ReplaceToken> transformations(String source);
}
