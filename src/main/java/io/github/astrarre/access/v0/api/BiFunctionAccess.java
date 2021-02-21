package io.github.astrarre.access.v0.api;

import com.google.common.collect.Iterators;
import io.github.astrarre.access.v0.api.func.AccessBiFunction;
import io.github.astrarre.access.v0.api.func.IterFunc;

public class BiFunctionAccess<A, B, C> extends Access<AccessBiFunction<A, B, C>, C> {
	/**
	 * combines {@link BiFunctionAccess (AccessFunction)} and {@link BiFunctionAccess (BinaryOperator)}
	 */
	public BiFunctionAccess() {
		this(functions -> (a, b) -> {
			for (AccessBiFunction<A, B, C> function : functions) {
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
	public BiFunctionAccess(IterFunc<AccessBiFunction<A, B, C>> iterFunc) {
		super(iterFunc);
	}

	/**
	 * @param combiner combines the return value of the function
	 */
	public static <A, B, C> BiFunctionAccess<A, B, C> newInstance(IterFunc<C> combiner) {
		return new BiFunctionAccess<>(functions -> (a, b) -> combiner.combine(() -> Iterators.transform(functions.iterator(), input -> input.apply(a, b))));
	}
}
