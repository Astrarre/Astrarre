package io.github.astrarre.access.v0.api.util;

import java.util.ArrayList;
import java.util.List;

import io.github.astrarre.util.v0.api.func.IterFunc;

public final class FunctionCompiler<F> {
	private final IterFunc<F> compiler;
	private final F empty;

	private List<F> functions;
	private F compiled;

	public FunctionCompiler(IterFunc<F> compiler, F empty) {
		this.compiler = compiler;
		this.empty = empty;
	}

	public void add(F function) {
		if (this.functions == null) {
			this.functions = new ArrayList<>();
		}
		this.functions.add(function);
		this.compiled = null;
	}

	public boolean isEmpty() {
		return this.functions == null;
	}

	public F get() {
		if (this.functions == null) {
			return this.empty;
		} else {
			F compiled = this.compiled;
			if(compiled == null) {
				this.compiled = compiled = this.compiler.combine(this.functions);
			}
			return compiled;
		}
	}
}
