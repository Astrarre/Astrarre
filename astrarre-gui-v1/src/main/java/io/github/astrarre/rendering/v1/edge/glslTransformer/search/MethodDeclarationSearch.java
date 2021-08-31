package io.github.astrarre.rendering.v1.edge.glslTransformer.search;

public class MethodDeclarationSearch extends MethodInvokeSearch {
	/**
	 * glsl doesn't have method overriding or instance methods, or even namespaces for that matter. So just the name will suffice
	 */
	public MethodDeclarationSearch(String name) {
		super(name);
	}

	@Override
	boolean check(String input, int index) {
		int paran = input.indexOf(")", index);
		if(paran == -1) return false;
		int semi = findIgnoreWhitespace(input, paran + 1, (str, offset) -> str.charAt(offset) == '{');
		return super.check(input, index) && semi != -1;
	}

	@Override
	boolean validBrackets(int brackets) {
		return brackets == 0;
	}

	@Override
	public String toString() {
		return "declare " + this.methodName;
	}
}
