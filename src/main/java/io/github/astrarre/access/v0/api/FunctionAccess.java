package io.github.astrarre.access.v0.api;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BinaryOperator;

import io.github.astrarre.access.internal.util.MapFilter;
import io.github.astrarre.access.v0.api.func.AccessFunction;
import io.github.astrarre.access.v0.api.provider.Provider;

public class FunctionAccess<A, B> extends Access<AccessFunction<A, B>, B> {
	private final MapFilter<A, AccessFunction<A, B>, B> instanceFunctions;
	private final MapFilter<Class<? extends A>, AccessFunction<A, B>, B> classFunctions;

	/**
	 * combines {@link FunctionAccess (AccessFunction)} and {@link FunctionAccess (BinaryOperator)}
	 */
	public FunctionAccess() {
		this(a -> null);
	}

	/**
	 * combines function by rejecting null return types
	 */
	public FunctionAccess(AccessFunction<A, B> defaultAccess) {
		this((function, function2) -> a -> {
			B val = function.apply(a);
			if (val != null) {
				return val;
			}
			return function2.apply(a);
		}, defaultAccess);
	}

	/**
	 * {@inheritDoc}
	 */
	public FunctionAccess(BinaryOperator<AccessFunction<A, B>> andThen, AccessFunction<A, B> defaultAccess) {
		super(andThen, defaultAccess);
		this.instanceFunctions = new MapFilter<>(andThen, AccessFunction.empty());
		this.classFunctions = new MapFilter<>(andThen, AccessFunction.empty());
	}

	public FunctionAccess(B defaultValue) {
		this(a -> defaultValue);
	}

	/**
	 * defaults to a null value
	 */
	public FunctionAccess(BinaryOperator<AccessFunction<A, B>> andThen) {
		this(andThen, a -> null);
	}

	public FunctionAccess(BinaryOperator<AccessFunction<A, B>> andThen, B defaultValue) {
		this(andThen, a -> defaultValue);
	}

	/**
	 * @param combiner combines the return value of the function
	 */
	public static <A, B> FunctionAccess<A, B> newInstance(BinaryOperator<B> combiner) {
		return new FunctionAccess<>((function, function2) -> a -> combiner.apply(function.apply(a), function2.apply(a)));
	}

	public static <A, B> FunctionAccess<A, B> newInstance(BinaryOperator<B> combiner, B defaultValue) {
		return new FunctionAccess<>((function, function2) -> a -> combiner.apply(function.apply(a), function2.apply(a)), defaultValue);
	}

	/**
	 * adds a function for {@link Provider}
	 */
	public FunctionAccess<A, B> addProviderFunction() {
		this.andThen(a -> {
			if(a instanceof Provider) {
				return ((Provider) a).get(this);
			}
			return null;
		});
		return this;
	}
	/**
	 * filters the access function for only objects that are {@link Object#equals(Object)} to the passed object
	 */
	public FunctionAccess<A, B> forInstance(A a, AccessFunction<A, B> function) {
		if(this.instanceFunctions.add(a, function)) {
			this.andThen(val -> this.instanceFunctions.get(val).apply(val));
		}
		return this;
	}

	/**
	 * only accepts if the passed value's class is the exact same the passed type.
	 * This means this will <b>NOT</b> obey inheritance!
	 * @param type the type
	 */
	public <C extends A> FunctionAccess<A, B> forClassExact(Class<C> type, AccessFunction<C, B> function) {
		if(this.classFunctions.add(type, (AccessFunction<A, B>) function)) {
			this.andThen(val -> this.classFunctions.get((Class<? extends A>) val.getClass()).apply(val));
		}
		return this;
	}
}
