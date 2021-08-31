package io.github.astrarre.rendering.v1.edge.glslTransformer.search;

import io.github.astrarre.rendering.v1.edge.glslTransformer.Token;
import it.unimi.dsi.fastutil.chars.CharOpenHashSet;
import it.unimi.dsi.fastutil.chars.CharSet;
import it.unimi.dsi.fastutil.chars.CharSets;

public class MethodInvokeSearch implements Search {
	static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
	static final String IDENTIFIER = ALPHABET.toUpperCase() + ALPHABET + "0123456789";
	static final CharSet IDENTIFIERS = CharSets.unmodifiable(new CharOpenHashSet(IDENTIFIER.toCharArray()));
	public final String methodName;
	/**
	 * glsl doesn't have method overriding or instance methods, or even namespaces for that matter. So just the name will suffice
	 */
	public MethodInvokeSearch(String name) {
		this.methodName = name;
	}

	public static void main(String[] args) {
		String h = "void bruh();\n\nvoid h() {\n\tbruh();\n}\nvoid bruh() {}".replace("\\\n", "\n");
		MethodPrototypeSearch proto = new MethodPrototypeSearch("bruh");
		System.out.println(proto.find(h));
		MethodDeclarationSearch declare = new MethodDeclarationSearch("h");
		System.out.println(declare.find(h));
		MethodInvokeSearch invoke = new MethodInvokeSearch("bruh");
		System.out.println(invoke.find(h));
		MethodDeclarationSearch declare2 = new MethodDeclarationSearch("bruh");
		System.out.println(declare2.find(h));
	}

	@Override
	public Token find(String s) {
		int c = 0;
		while((c = s.indexOf(this.methodName, c + 1)) != -1) {
			if(IDENTIFIERS.contains(s.charAt(c - 1))) {
				continue;
			}
			int index = findIgnoreWhitespace(s, c + this.methodName.length(), (e, o) -> e.charAt(o) == '(');
			if(index != -1 && !isCommentedOut(s, index, true) && this.check(s, index)) {
				return new Token(c, this.methodName.length());
			}
		}
		throw new UnsupportedOperationException("unable to find " + this);
	}

	public static boolean isCommentedOut(String s, int index, boolean includeMacros) {
		boolean multiLine = false, line = false;
		for(int i = 0; i < index; i++) {
			if(multiLine) {
				if(s.startsWith("*/", i)) {
					multiLine = false;
					i++; // skip the '/'
				}
			} else if(line) {
				if(s.charAt(i) == '\n') {
					line = false;
				}
			} else if(s.charAt(i) == '#') {
				line = includeMacros;
			} else if(s.startsWith("//", i)) {
				line = true;
				i++;
			} else if(s.startsWith("/*", i)) {
				multiLine = true;
				i++; // skip the '*'
			}
		}

		return multiLine || line;
	}

	public static int findIgnoreWhitespace(String str, int index, Predicate pred) {
		for(int i = index; i < str.length(); i++) {
			char c = str.charAt(i);
			if(!Character.isWhitespace(c)) {
				if(pred.matches(str, i)) {
					return i;
				} else {
					return -1;
				}
			}
		}
		return -1;
	}

	@Override
	public String toString() {
		return "invoke " + this.methodName + "()";
	}

	boolean check(String input, int index) {
		int brackets = 0;
		for(int i = 0; i < index; i++) {
			char c = input.charAt(i);
			if(c == '{') {
				brackets++;
			} else if(c == '}') {
				brackets--;
			}
		}

		return this.validBrackets(brackets);
	}

	boolean validBrackets(int brackets) {
		return brackets == 1;
	}

	interface Predicate {
		boolean matches(String str, int offset);
	}

	public static record Index(int lineNumber, int index) {}
}
