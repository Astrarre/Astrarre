package io.github.astrarre.access.v0.fabric.helper;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import io.github.astrarre.access.v0.api.helper.FunctionAccessHelper;
import io.github.astrarre.access.v0.api.util.FunctionCompiler;
import io.github.astrarre.util.v0.api.func.IterFunc;

import net.minecraft.tag.Tag;

public class TaggedAccessHelper<T, F> {
	protected final Map<Tag<T>, FunctionCompiler<F>> functions = new HashMap<>();
	protected final Consumer<Function<T, F>> functionAdder;
	protected final IterFunc<F> combine;
	protected final F empty;
	protected final Function<Tag<T>, FunctionCompiler<F>> init = this::getCompiler;

	/**
	 * creates a new function helper who's incoming type is not the same as the type being filtered
	 */
	public static <I, T, F> TaggedAccessHelper<T, F> create(IterFunc<F> func, Consumer<Function<I, F>> adder, Function<I, T> mapper, F empty) {
		return new TaggedAccessHelper<>(func, function -> adder.accept(i -> function.apply(mapper.apply(i))), empty);
	}

	/**
	 * creates a new function helper who's incoming type is not the same as the type being filtered
	 */
	public static <I, T, F> TaggedAccessHelper<T, F> create(IterFunc<F> func, Consumer<Function<I, F>> adder, Function<I, T> mapper) {
		return new TaggedAccessHelper<>(func, function -> adder.accept(i -> function.apply(mapper.apply(i))), null);
	}

	public TaggedAccessHelper(IterFunc<F> func, Consumer<Function<T, F>> functionAdder, F empty) {
		this.combine = func;
		this.functionAdder = functionAdder;
		this.empty = empty;
	}

	public TaggedAccessHelper<T, F> forTag(Tag<T> tag, F function) {
		this.functions.computeIfAbsent(tag, this.init).add(function);
		return this;
	}

	private FunctionCompiler<F> getCompiler(Tag<T> a) {
		FunctionCompiler<F> compiler = new FunctionCompiler<>(this.combine, this.empty);
		this.functionAdder.accept(i -> {
			if (a.contains(i)) {
				return compiler.get();
			} else {
				return null;
			}
		});
		return compiler;
	}
}
