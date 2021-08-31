package io.github.astrarre.rendering.v1.edge.glslTransformer.search;

import io.github.astrarre.rendering.v1.edge.glslTransformer.Token;
import org.jetbrains.annotations.Nullable;

/**
 * @param from inclusive
 */
public record ConstrainedSearch(Search search, Search from, @Nullable Search to) implements Search {
	@Override
	public Token find(String input) {
		Token from;
		try {
			from = this.from.find(input);
		} catch(Exception e) {
			throw new IllegalStateException("unable to find minimum bounds for " + this);
		}
		String str = input.substring(from.end());
		Token at = this.search.find(str);
		if(this.to != null) {
			try {
				this.to.find(str.substring(at.end()));
			} catch(Exception e) {
				throw new IllegalStateException("unable to find maximum bounds for " + this);
			}
		}
		return at;
	}
}
