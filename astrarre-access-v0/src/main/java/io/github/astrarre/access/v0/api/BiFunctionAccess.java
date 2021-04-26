package io.github.astrarre.access.v0.api;

import java.util.function.BiFunction;

import com.google.common.collect.Iterators;
import io.github.astrarre.util.v0.api.func.IterFunc;
import io.github.astrarre.util.v0.api.Id;

public class BiFunctionAccess<A, B, C> extends Access<BiFunction<A, B, C>> {
	/**
	 * combines {@link BiFunctionAccess (AccessFunction)} and {@link BiFunctionAccess (BinaryOperator)}
	 */
	public BiFunctionAccess(Id id) {
		this(id, functions -> (a, b) -> {
			for (BiFunction<A, B, C> function : functions) {
				C ret = function.apply(a, b);
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
	public BiFunctionAccess(Id id, IterFunc<BiFunction<A, B, C>> iterFunc) {
		super(id, iterFunc);
	}

	/**
	 * @param combiner combines the return value of the function
	 */
	public static <A, B, C> BiFunctionAccess<A, B, C> newInstance(Id id, IterFunc<C> combiner) {
		return new BiFunctionAccess<>(id, functions -> (a, b) -> combiner.combine(() -> Iterators.transform(
				functions.iterator(),
				input -> input.apply(a, b))));
	}
}
