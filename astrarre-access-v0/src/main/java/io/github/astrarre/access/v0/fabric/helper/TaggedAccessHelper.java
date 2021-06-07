package io.github.astrarre.access.v0.fabric.helper;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import io.github.astrarre.access.v0.api.util.FunctionCompiler;
import io.github.astrarre.util.v0.api.func.IterFunc;

import net.minecraft.tag.Tag;

public class TaggedAccessHelper<I, T, F> {
	protected final Map<Tag<T>, FunctionCompiler<F>> functions = new HashMap<>();
	protected final Function<Tag<T>, FunctionCompiler<F>> init = this::getCompiler;
	protected final Consumer<Function<I, F>> functionAdder;
	protected final IterFunc<F> combine;
	protected final Function<I, T> extract;
	protected final F empty;

	public TaggedAccessHelper(IterFunc<F> func, Consumer<Function<I, F>> functionAdder, Function<I, T> extract, F empty) {
		this.combine = func;
		this.extract = extract;
		this.functionAdder = functionAdder;
		this.empty = empty;
	}

	public TaggedAccessHelper<I, T, F> forTag(Tag<T> tag, F function) {
		this.functions.computeIfAbsent(tag, this.init).add(function);
		return this;
	}

	private FunctionCompiler<F> getCompiler(Tag<T> a) {
		FunctionCompiler<F> compiler = new FunctionCompiler<>(this.combine, this.empty);
		this.functionAdder.accept(i -> {
			if(a.contains(this.extract.apply(i))) {
				return compiler.get();
			} else {
				return null;
			}
		});
		return compiler;
	}
}
