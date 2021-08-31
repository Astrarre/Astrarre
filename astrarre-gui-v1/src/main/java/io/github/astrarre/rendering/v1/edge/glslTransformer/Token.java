package io.github.astrarre.rendering.v1.edge.glslTransformer;

import io.github.astrarre.rendering.v1.edge.glslTransformer.search.MethodInvokeSearch;

public record Token(int from, int len) {
	public static final Token START = new Token(0, 0);
	public static final Token INVALID = new Token(-1, -1);

	public int end() {
		return this.from + this.len;
	}

	public String replace(String buffer, String value) {
		if(this.from == buffer.length() && this.len == 0) {
			return buffer + value;
		} else if(this.from == 0 && this.len == 0) {
			return value + buffer;
		} else {
			return buffer.substring(0, this.from) + value + buffer.substring(this.end());
		}
	}

	public static void main(String[] args) {
		String exampleShader = "void main() {float h = sin(.5f);}";
		var search = new MethodInvokeSearch("sin");
		System.out.println(search.find(exampleShader).replace(exampleShader, "bruh"));
	}
}
