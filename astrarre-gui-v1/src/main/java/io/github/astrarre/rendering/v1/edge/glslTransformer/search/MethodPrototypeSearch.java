package io.github.astrarre.rendering.v1.edge.glslTransformer.search;

import io.github.astrarre.rendering.v1.edge.glslTransformer.search.MethodInvokeSearch;

public class MethodPrototypeSearch extends MethodInvokeSearch {
	/**
	 * glsl doesn't have method overriding or instance methods, or even namespaces for that matter. So just the name will suffice
	 */
	public MethodPrototypeSearch(String name) {
		super(name);
	}

	@Override
	boolean check(String input, int index) {
		int paran = input.indexOf(")", index);
		if(paran == -1) return false;
		int semi = findIgnoreWhitespace(input, paran + 1, (str, offset) -> str.charAt(offset) == ';');
		return super.check(input, index) && semi != -1;
	}

	@Override
	boolean validBrackets(int brackets) {
		return brackets == 0;
	}
}
