package io.github.astrarre.rendering.v1.edge.glslTransformer.search;

import io.github.astrarre.rendering.v1.edge.glslTransformer.Token;
import io.github.astrarre.util.v0.api.Validate;

public record LineBeforeSearch(Search search) implements Search {
	@Override
	public Token find(String input) {
		Token token = this.search.find(input);
		return new Token(Validate.greaterThan(input.lastIndexOf('\n', token.from()) - 1, 0, "line before"), 0);
	}
}
