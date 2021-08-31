package io.github.astrarre.rendering.v1.edge.glslTransformer.inject;

import java.util.List;

import io.github.astrarre.rendering.v1.edge.glslTransformer.ReplaceToken;
import io.github.astrarre.rendering.v1.edge.glslTransformer.search.Search;

public record InjectTransform(Search search, String inject) implements Transform {
	@Override
	public List<ReplaceToken> transformations(String source) {
		return List.of(new ReplaceToken(this.inject, this.search.find(source).from()-1, 0));
	}
}
