package io.github.astrarre.rendering.v1.edge.glslTransformer.search;

import io.github.astrarre.rendering.v1.edge.glslTransformer.Token;

/**
 * @see OrdinalSearch
 * @see ConstrainedSearch
 * @see MethodInvokeSearch
 */
public interface Search {
	static Search end() {
		return input -> new Token(input.length(), 0);
	}

	static Search start() {
		return (input) -> Token.START;
	}

	static Search methodInvoke(String string) {
		return new MethodInvokeSearch(string);
	}

	/**
	 * not recommended
	 */
	static Search line(int line) {
		return (input) -> {
			int index = 0;
			for(int i = 0; i < line; i++) {
				index = input.indexOf('\n', index);
				if(index == -1) {
					throw new IllegalStateException("not enough lines in " + input + " (ln: " + line+")");
				}
			}
			return new Token(index+1, 0);
		};
	}

	Token find(String input);

	default Search ordinal(int ordinal) {
		return new OrdinalSearch(this, ordinal);
	}

	default Search from(Search from) {
		return new ConstrainedSearch(this, from, null);
	}
}
