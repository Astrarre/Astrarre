package io.github.astrarre.access.v0.api.helper;

import java.util.function.Consumer;
import java.util.function.Function;

import com.google.common.collect.MapMaker;
import com.google.common.reflect.TypeToken;
import io.github.astrarre.access.internal.CompiledFunctionClassValue;
import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.access.v0.api.MapFilter;
import io.github.astrarre.access.v0.api.provider.GenericProvider;
import io.github.astrarre.access.v0.api.util.FunctionCompiler;
import io.github.astrarre.util.v0.api.Id;
import io.github.astrarre.util.v0.api.func.IterFunc;

/**
 * Aspect-Oriented version of FunctionAccess.
 *
 * @param <T> the type to filter (eg. Block)
 * @param <F> the function type
 */
public class FunctionAccessHelper<T, F> extends AbstractAccessHelper<T, F> {
	protected final MapFilter<T, F> filterStrong, filterWeak, filterExact;
	protected final CompiledFunctionClassValue<F> filterClassExact;
	protected boolean addedDirectImplementation, addedGenericProvider;

	public FunctionAccessHelper(AccessHelpers.Context<T, F> copyFrom) {
		super(copyFrom);
		this.filterStrong = new MapFilter<>(this.iterFunc, this.empty, false);
		this.filterWeak = new MapFilter<>(this.iterFunc, this.empty, true);
		this.filterExact = new MapFilter<>(this.iterFunc, this.empty, () -> new MapMaker().weakKeys().makeMap());
		this.filterClassExact = new CompiledFunctionClassValue<>(this.iterFunc, this.empty);
	}

	/**
	 * if {@link T} instance of {@link F}, returns {@code (F) t}.
	 *
	 * @param functionType the exact type of the function, used to filter. This is a type token to allow the helper to differentiate between {@link
	 *        Consumer<Integer>} and {@link Consumer<String>} for example.
	 */
	public FunctionAccessHelper<T, F> forDirectImplementation(TypeToken<F> functionType) {
		if(!this.addedDirectImplementation) {
			this.addedDirectImplementation = true;
			this.andThen.accept(t -> {
				if(functionType.isSupertypeOf(t.getClass())) {
					return (F) t;
				} else {
					return this.empty;
				}
			});
		}
		return this;
	}

	/**
	 * adds support for {@link GenericProvider}
	 *
	 * @param access the access this function helper
	 * @see GenericProvider
	 */
	@SuppressWarnings("ALL")
	public FunctionAccessHelper<T, F> forGenericProvider(Access<F> access) {
		if(!this.addedGenericProvider) {
			this.addedGenericProvider = true;
			this.andThen.accept(t -> {
				F func;
				if(t instanceof GenericProvider<?> g && (func = (F) g.get((Access) access)) != null) {
					return func;
				}
				return this.empty;
			});
		}
		return this;
	}

	/**
	 * The access holds a weak reference to the object, if the incoming object is {@code ==}, then applies the passed function. This is useful for
	 * classes that shouldn't implement {@link Object#equals(Object)}.
	 */
	public FunctionAccessHelper<T, F> forInstanceExact(T instance, F function) {
		if(this.filterExact.add(instance, function)) {
			this.andThen.accept(this.filterExact::get);
		}
		return this;
	}

	/**
	 * The access holds a weak reference to the object, if the incoming object is {@link Object#equals(Object)}, then applies the passed function.
	 * This is useful for classes that don't implement {@link Object#equals(Object)}, as when they are GCed, they're unreachable by the filter
	 * anyways.
	 */
	public FunctionAccessHelper<T, F> forInstanceWeak(T instance, F function) {
		if(this.filterWeak.add(instance, function)) {
			this.andThen.accept(this.filterWeak::get);
		}
		return this;
	}

	/**
	 * Holds a strong reference to the object, if the incoming object is {@link Object#equals(Object)}, then applies the passed function. This is
	 * useful for classes that implement {@link Object#equals(Object)} such as {@link Id}.
	 *
	 * @see #forInstanceWeak(Object, Object)
	 */
	public FunctionAccessHelper<T, F> forInstanceStrong(T instance, F function) {
		if(this.filterStrong.add(instance, function)) {
			this.andThen.accept(this.filterStrong::get);
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
			this.andThen.accept(t -> this.filterClassExact.get(t.getClass()).get());
		}
		compiler.add(function);
		return this;
	}
}
