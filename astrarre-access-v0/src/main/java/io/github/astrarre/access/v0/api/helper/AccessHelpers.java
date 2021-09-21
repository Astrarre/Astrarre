package io.github.astrarre.access.v0.api.helper;

import java.util.function.Function;

import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.access.v0.api.util.FilteredFunc;
import io.github.astrarre.util.v0.api.func.IterFunc;

public class AccessHelpers {
	public static <F, I> Context<I, F> ctx(IterFunc<F> func, FilteredFunc.Adding<I, F> andThen, F empty) {
		return new ContextImpl<>(func, andThen, empty);
	}

	public static <F, I> Context<I, F> ctx(IterFunc<F> func, FilteredFunc.Adding<I, F> and) {
		return ctx(func, and, func.empty());
	}

	public static <F, I> Context<I, F> ctx(Access<F> func, FilteredFunc<I, F> and, F empty) {
		return ctx(func.combiner, f -> func.andThen(and.apply(f)), empty);
	}

	public static <F, I> Context<I, F> ctx(Access<F> access, FilteredFunc<I, F> and) {
		return ctx(access, and, access.combiner.empty());
	}

	public static <F, I> Context<I, F> ctx(IterFunc<F> func, Access<F> access, FilteredFunc<I, F> and) {
		return ctx(func, and.then(access), func.empty());
	}

	public static <F, I> Context<I, F> ctx(FilteredFunc.AddingImpl<I, F> and) {
		return ctx(and.access(), and.filter());
	}

	public static <F, I> Context<I, F> ctx(AbstractAccessHelper<I, F> copyFrom) {
		return ctx(copyFrom.iterFunc, copyFrom.andThen, copyFrom.empty);
	}

	public interface Context<I, F> {
		IterFunc<F> func();

		FilteredFunc.Adding<I, F> andThen();

		F empty();

		default <T> ContextImpl<T, F> map(Function<I, T> mapper) {
			return new ContextImpl<>(this.func(), function -> this.andThen().accept(i -> function.apply(mapper.apply(i))), this.empty());
		}
	}

	public record ContextImpl<I, F>(IterFunc<F> func, FilteredFunc.Adding<I, F> andThen, F empty) implements Context<I, F> {
	}
}
