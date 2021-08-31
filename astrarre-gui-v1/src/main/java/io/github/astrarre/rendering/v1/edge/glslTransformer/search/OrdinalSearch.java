package io.github.astrarre.rendering.v1.edge.glslTransformer.search;

import io.github.astrarre.rendering.v1.edge.glslTransformer.Token;

public record OrdinalSearch(Search search, int ordinal) implements Search {
	@Override
	public Token find(String input) {
		Token ln = this.search.find(input);
		for(int i = 0; i < this.ordinal; i++) {
			ln = this.search.find(input.substring(ln.end() + 1));
		}
		return ln;
	}
}
