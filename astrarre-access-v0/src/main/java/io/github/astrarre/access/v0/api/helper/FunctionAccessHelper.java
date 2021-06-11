package io.github.astrarre.access.v0.api.helper;

import java.util.function.Consumer;
import java.util.function.Function;

import io.github.astrarre.access.internal.CompiledFunctionClassValue;
import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.access.v0.api.FunctionAccess;
import io.github.astrarre.access.v0.api.MapFilter;
import io.github.astrarre.access.v0.api.util.FunctionCompiler;
import io.github.astrarre.util.v0.api.Id;
import io.github.astrarre.util.v0.api.func.ArrayFunc;
import io.github.astrarre.util.v0.api.func.IterFunc;

/**
 * Aspect-Oriented version of FunctionAccess.
 * @param <T> the type to filter (eg. Block)
 * @param <F> the function type
 */
public class FunctionAccessHelper<T, F> {
	protected final MapFilter<T, F> filterStrong, filterWeak;
	protected final CompiledFunctionClassValue<F> filterClassExact;
	protected final Consumer<Function<T, F>> functionAdder;

	/**
	 * creates a new function helper who's incoming type is not the same as the type being filtered
	 */
	public static <I, T, F> FunctionAccessHelper<T, F> create(IterFunc<F> func, Consumer<Function<I, F>> adder, Function<I, T> mapper, F empty) {
		return new FunctionAccessHelper<>(func, function -> adder.accept(i -> function.apply(mapper.apply(i))), empty);
	}

	/**
	 * creates a new function helper who's incoming type is not the same as the type being filtered
	 */
	public static <I, T, F> FunctionAccessHelper<T, F> create(IterFunc<F> func, Consumer<Function<I, F>> adder, Function<I, T> mapper) {
		return new FunctionAccessHelper<>(func, function -> adder.accept(i -> function.apply(mapper.apply(i))), null);
	}

	public FunctionAccessHelper(Access<F> func, Function<Function<T, F>, F> adder) {
		this(func.combiner, f -> func.andThen(adder.apply(f)), null);
	}

	public FunctionAccessHelper(IterFunc<F> func, Consumer<Function<T, F>> adder) {
		this(func, adder, null);
	}

	/**
	 * @param func the iter func passed in the constructor
	 * @param adder Registers a function in the access. The passed function provides the method to find the function with the given value, may return null.
	 */
	public FunctionAccessHelper(IterFunc<F> func, Consumer<Function<T, F>> adder, F empty) {
		this.filterStrong = new MapFilter<>(func, empty, false);
		this.filterWeak = new MapFilter<>(func, empty, true);
		this.filterClassExact = new CompiledFunctionClassValue<>(func, empty);
		this.functionAdder = adder;
	}

	/**
	 * The access holds a weak reference to the object, if the incoming object is {@link Object#equals(Object)}, then applies the passed function.
	 * This is useful for classes that don't implement {@link Object#equals(Object)}, as when they are GCed, they're unreachable by the filter anyways.
	 */
	public FunctionAccessHelper<T, F> forInstanceWeak(T instance, F function) {
		if(this.filterWeak.add(instance, function)) {
			this.functionAdder.accept(this.filterWeak::get);
		}
		return this;
	}

	/**
	 * Holds a strong reference to the object, if the incoming object is {@link Object#equals(Object)}, then applies the passed function.
	 * This is useful for classes that implement {@link Object#equals(Object)} such as {@link Id}.
	 * @see #forInstanceWeak(Object, Object)
	 */
	public FunctionAccessHelper<T, F> forInstanceStrong(T instance, F function) {
		if(this.filterStrong.add(instance, function)) {
			this.functionAdder.accept(this.filterStrong::get);
		}
		return this;
	}

	/**
	 * If the incoming object's class is equal to the passed class, then it applies the passed function. This is <b>NOT</b> the same as instanceof.
	 * The reason instanceof is not provided, is because it is O(N) access and can't be optimized.
	 */
	public FunctionAccessHelper<T, F> forClassExact(Class<? extends T> instance, F function) {
		FunctionCompiler<F> compiler = this.filterClassExact.get(instance);
		if(compiler.isEmpty()) {
			this.functionAdder.accept(t -> this.filterClassExact.get(t.getClass()).get());
		}
		compiler.add(function);
		return this;
	}
}
