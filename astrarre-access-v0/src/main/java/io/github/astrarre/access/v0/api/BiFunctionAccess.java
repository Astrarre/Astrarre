package io.github.astrarre.access.v0.api;

import java.util.function.BiFunction;

import io.github.astrarre.access.v0.api.helper.FunctionAccessHelper;
import io.github.astrarre.util.v0.api.Id;
import io.github.astrarre.util.v0.api.func.ArrayFunc;
import io.github.astrarre.util.v0.api.func.IterFunc;

public class BiFunctionAccess<A, B, C> extends Access<BiFunction<A, B, C>> {
	private final FunctionAccessHelper<A, BiFunction<A, B, C>> aHelper;
	private final FunctionAccessHelper<B, BiFunction<A, B, C>> bHelper;

	/**
	 * combines {@link BiFunctionAccess (AccessFunction)} and {@link BiFunctionAccess (BinaryOperator)}
	 */
	public BiFunctionAccess(Id id) {
		this(id, functions -> (a, b) -> {
			for(BiFunction<A, B, C> function : functions) {
				C ret = function.apply(a, b);
				if(ret != null) {
					return ret;
				}
			}
			return null;
		}, (a, b) -> null);
	}

	public BiFunctionAccess(Id id, ArrayFunc<BiFunction<A, B, C>> iterFunc) {
		this(id, iterFunc, iterFunc.empty());
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public BiFunctionAccess(Id id, ArrayFunc<BiFunction<A, B, C>> iterFunc, BiFunction<A, B, C> empty) {
		super(id, iterFunc);
		this.aHelper = new FunctionAccessHelper<>(this.funcFilter_((Class<A>) Object.class, 0, empty));
		this.bHelper = new FunctionAccessHelper<>(this.funcFilter_((Class<B>) Object.class, 1, empty));
	}

	public BiFunctionAccess<A, B, C> addProviderFunction() {
		this.aHelper.forGenericProvider(this);
		this.bHelper.forGenericProvider(this);
		return this;
	}

	/**
	 * advanced filtering for the first passed instance
	 */
	public FunctionAccessHelper<A, BiFunction<A, B, C>> getAHelper() {
		return this.aHelper;
	}

	/**
	 * advanced filtering for the second passed instance
	 */
	public FunctionAccessHelper<B, BiFunction<A, B, C>> getBHelper() {
		return this.bHelper;
	}

	/**
	 * @param combiner combines the return value of the function
	 */
	public static <A, B, C> BiFunctionAccess<A, B, C> newInstance(Id id, IterFunc<C> combiner) {
		return new BiFunctionAccess<>(id, functions -> (a, b) -> transform(functions, f -> f.apply(a, b), combiner));
	}
}
