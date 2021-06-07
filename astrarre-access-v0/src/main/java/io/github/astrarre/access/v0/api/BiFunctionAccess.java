package io.github.astrarre.access.v0.api;

import java.util.function.BiFunction;
import java.util.function.Function;

import com.google.common.collect.Iterators;
import io.github.astrarre.access.v0.api.helper.FunctionAccessHelper;
import io.github.astrarre.util.v0.api.Validate;
import io.github.astrarre.util.v0.api.func.IterFunc;
import io.github.astrarre.util.v0.api.Id;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

public class BiFunctionAccess<A, B, C> extends Access<BiFunction<A, B, C>> {
	private final FunctionAccessHelper<A, A, BiFunction<A, B, C>> aHelper;
	private final FunctionAccessHelper<B, B, BiFunction<A, B, C>> bHelper;

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
		this.aHelper = new FunctionAccessHelper<>(iterFunc, function -> this.andThen((a, b) -> function.apply(a).apply(a, b)), Function.identity(), (a, b) -> null);
		this.bHelper = new FunctionAccessHelper<>(iterFunc, function -> this.andThen((a, b) -> function.apply(b).apply(a, b)), Function.identity(), (a, b) -> null);
	}

	/**
	 * advanced filtering for the first passed instance
	 */
	public FunctionAccessHelper<A, A, BiFunction<A, B, C>> getAHelper() {
		return this.aHelper;
	}

	/**
	 * advanced filtering for the second passed instance
	 */
	public FunctionAccessHelper<B, B, BiFunction<A, B, C>> getBHelper() {
		return this.bHelper;
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
