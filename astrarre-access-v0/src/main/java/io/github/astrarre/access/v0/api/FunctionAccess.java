package io.github.astrarre.access.v0.api;

import java.util.Objects;
import java.util.function.Function;

import com.google.common.collect.Iterators;
import io.github.astrarre.access.internal.MapFilter;
import io.github.astrarre.access.v0.api.func.IterFunc;
import io.github.astrarre.access.v0.api.provider.Provider;

public class FunctionAccess<A, B> extends Access<Function<A, B>> {
	private final MapFilter<A, Function<A, B>> instanceFunctions;
	private final MapFilter<Class<? extends A>, Function<A, B>> classFunctions;
	private boolean addedProviderFunction;

	/**
	 * combines {@link FunctionAccess (AccessFunction)} and {@link FunctionAccess (BinaryOperator)}
	 */
	public FunctionAccess() {
		this(a -> val -> {
			for (Function<A, B> function : a) {
				B ret = function.apply(val);
				if (ret != null) {
					return ret;
				}
			}
			return null;
		});
	}

	/**
	 * {@inheritDoc}
	 */
	public FunctionAccess(IterFunc<Function<A, B>> iterFunc) {
		super(iterFunc);
		this.instanceFunctions = new MapFilter<>(iterFunc, true);
		this.classFunctions = new MapFilter<>(iterFunc, true);
	}

	/**
	 * @param combiner combines the return value of the function
	 */
	public static <A, B> FunctionAccess<A, B> newInstance(IterFunc<B> combiner) {
		return new FunctionAccess<>(functions -> a -> combiner.combine(() -> Iterators.filter(Iterators.transform(functions.iterator(),
				input -> input.apply(a)), Objects::nonNull)));
	}

	/**
	 * adds a function for {@link Provider}
	 *
	 * (calling this multiple times will only register it once)
	 */
	public FunctionAccess<A, B> addProviderFunction() {
		if (this.addedProviderFunction) {
			return this;
		}
		this.addedProviderFunction = true;

		this.andThen(a -> {
			if (a instanceof Provider) {
				return (B) ((Provider) a).get(this);
			}
			return null;
		});
		return this;
	}

	/**
	 * filters the access function for only objects that are {@link Object#equals(Object)} to the passed object
	 */
	public FunctionAccess<A, B> forInstance(A a, Function<A, B> function) {
		if (this.instanceFunctions.add(a, function)) {
			this.andThen(val -> this.instanceFunctions.get(val).apply(val));
		}
		return this;
	}

	/**
	 * only accepts if the passed value's class is the exact same the passed type. This means this will <b>NOT</b> obey inheritance!
	 *
	 * @param type the type
	 */
	public <C extends A> FunctionAccess<A, B> forClassExact(Class<C> type, Function<C, B> function) {
		if (this.classFunctions.add(type, (Function<A, B>) function)) {
			this.andThen(val -> this.classFunctions.get((Class<? extends A>) val.getClass()).apply(val));
		}
		return this;
	}
}
