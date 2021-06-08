package io.github.astrarre.access.internal;

import io.github.astrarre.access.v0.api.util.FunctionCompiler;
import io.github.astrarre.util.v0.api.func.IterFunc;

public class CompiledFunctionClassValue<F> extends ClassValue<FunctionCompiler<F>> {
	private final IterFunc<F> compiler;
	private final F empty;

	public CompiledFunctionClassValue(IterFunc<F> compiler, F empty) {
		this.compiler = compiler;
		this.empty = empty;
	}

	@Override
	protected FunctionCompiler<F> computeValue(Class<?> type) {
		return new FunctionCompiler<>(this.compiler, this.empty);
	}
}
