package io.github.astrarre.rendering.v1.edge.glslTransformer.inject;

import java.util.List;

import io.github.astrarre.rendering.v1.edge.glslTransformer.ReplaceToken;
import io.github.astrarre.rendering.v1.edge.glslTransformer.search.Search;

public record ReplaceTransform(Search search, String replacement) implements Transform {
	@Override
	public List<ReplaceToken> transformations(String source) {
		return List.of(new ReplaceToken(this.search.find(source), this.replacement));
	}
}
