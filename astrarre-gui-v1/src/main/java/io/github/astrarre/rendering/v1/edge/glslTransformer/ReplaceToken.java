package io.github.astrarre.rendering.v1.edge.glslTransformer;

/**
 * @param length can be zero for injection
 */
public record ReplaceToken(String replacement, int offset, int length) {
	public ReplaceToken(Token token, String replacement) {
		this(replacement, token.from(), token.end());
	}
}
