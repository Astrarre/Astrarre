package io.github.astrarre.access.v0.fabric.helper;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.access.v0.api.helper.AbstractAccessHelper;
import io.github.astrarre.access.v0.api.util.FunctionCompiler;
import io.github.astrarre.util.v0.api.func.IterFunc;

import net.minecraft.tag.Tag;

public class TaggedAccessHelper<T, F> extends AbstractAccessHelper<T, F> {
	protected final Map<Tag<T>, FunctionCompiler<F>> functions = new HashMap<>();
	protected final Function<Tag<T>, FunctionCompiler<F>> init = this::getCompiler;

	public static <I, T, F> TaggedAccessHelper<T, F> create(IterFunc<F> func, Consumer<Function<I, F>> adder, Function<I, T> mapper, F empty) {
		return new TaggedAccessHelper<>(func, function -> adder.accept(i -> function.apply(mapper.apply(i))), empty);
	}

	public static <I, T, F> TaggedAccessHelper<T, F> create(AbstractAccessHelper<I, F> copyFrom, Function<I, T> mapper) {
		return new TaggedAccessHelper<>(copyFrom.iterFunc, function -> copyFrom.andThen.accept(i -> function.apply(mapper.apply(i))), copyFrom.empty);
	}

	public static <I, T, F> TaggedAccessHelper<T, F> create(IterFunc<F> func, Consumer<Function<I, F>> adder, Function<I, T> mapper) {
		return create(func, adder, mapper, null);
	}

	public static <I, T, F> TaggedAccessHelper<T, F> create(Access<F> func, Function<Function<I, F>, F> and, Function<I, T> mapper, F empty) {
		return new TaggedAccessHelper<>(func, function -> and.apply(i -> function.apply(mapper.apply(i))), empty);
	}

	public static <I, T, F> TaggedAccessHelper<T, F> create(Access<F> func, Function<Function<I, F>, F> and, Function<I, T> mapper) {
		return create(func, and, mapper, null);
	}

	public TaggedAccessHelper(AbstractAccessHelper<T, F> copyFrom) {
		this(copyFrom.iterFunc, copyFrom.andThen, copyFrom.empty);
	}

	public TaggedAccessHelper(Access<F> func, Function<Function<T, F>, F> and, F empty) {
		this(func.combiner, f -> func.andThen(and.apply(f)), empty);
	}

	public TaggedAccessHelper(Access<F> func, Function<Function<T, F>, F> adder) {
		this(func, adder, null);
	}

	public TaggedAccessHelper(IterFunc<F> func, Consumer<Function<T, F>> functionAdder, F empty) {
		super(func, functionAdder, empty);
	}

	public TaggedAccessHelper<T, F> forTag(Tag<T> tag, F function) {
		this.functions.computeIfAbsent(tag, this.init).add(function);
		return this;
	}

	private FunctionCompiler<F> getCompiler(Tag<T> a) {
		FunctionCompiler<F> compiler = new FunctionCompiler<>(this.iterFunc, this.empty);
		this.andThen.accept(i -> {
			if (a.contains(i)) {
				return compiler.get();
			} else {
				return null;
			}
		});
		return compiler;
	}
}
