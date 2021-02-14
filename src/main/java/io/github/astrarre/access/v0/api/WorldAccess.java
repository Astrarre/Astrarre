package io.github.astrarre.access.v0.api;

import java.util.function.BinaryOperator;

import io.github.astrarre.access.internal.util.MapFilter;
import io.github.astrarre.access.v0.api.func.WorldFunction;
import io.github.astrarre.access.v0.api.provider.BlockEntityProvider;
import io.github.astrarre.access.v0.api.provider.BlockProvider;
import io.github.astrarre.v0.block.Block;
import io.github.astrarre.v0.block.BlockState;
import io.github.astrarre.v0.block.entity.BlockEntityType;

public class WorldAccess<T> extends Access<WorldFunction<T>, T> {
	private final MapFilter<BlockEntityType<?>, WorldFunction<T>, T> blockEntityTypes;
	private final MapFilter<BlockState, WorldFunction<T>, T> blockStateTypes;
	private final MapFilter<Block, WorldFunction<T>, T> blockTypes;

	/**
	 * docs for each of the constructors are the same from FunctionAccess
	 *
	 * @see FunctionAccess
	 */
	public WorldAccess() {
		this((WorldFunction.NoBlock<T>) (direction, world, pos) -> null);
	}

	public WorldAccess(WorldFunction<T> defaultAccess) {
		this((function, function2) -> (direction, state, world, pos, entity) -> {
			T val = function.get(direction, state, world, pos, entity);
			if (val != null) {
				return val;
			}
			return function2.get(direction, state, world, pos, entity);
		}, defaultAccess);
	}

	public WorldAccess(BinaryOperator<WorldFunction<T>> andThen, WorldFunction<T> defaultAccess) {
		super(andThen, defaultAccess);
		this.blockEntityTypes = new MapFilter<>(andThen, WorldFunction.empty());
		this.blockStateTypes = new MapFilter<>(andThen, WorldFunction.empty());
		this.blockTypes = new MapFilter<>(andThen, WorldFunction.empty());
	}

	public WorldAccess(T defaultValue) {
		this((WorldFunction.NoBlock<T>) (d, w, p) -> defaultValue);
	}

	public WorldAccess(BinaryOperator<WorldFunction<T>> andThen) {
		this(andThen, (WorldFunction.NoBlock<T>) (d, w, p) -> null);
	}

	public WorldAccess(BinaryOperator<WorldFunction<T>> andThen, T defaultValue) {
		this(andThen, (WorldFunction.NoBlock<T>) (d, w, p) -> defaultValue);
	}

	public static <T> WorldAccess<T> newInstance(BinaryOperator<T> combiner) {
		return new WorldAccess<>((function, function2) -> (direction, state, world, pos, entity) -> combiner.apply(function.get(direction,
				state,
				world,
				pos,
				entity), function2.get(direction, state, world, pos, entity)));
	}

	public static <T> WorldAccess<T> newInstance(BinaryOperator<T> combiner, T defaultValue) {
		return new WorldAccess<>((function, function2) -> (direction, state, world, pos, entity) -> combiner.apply(function.get(direction,
				state,
				world,
				pos,
				entity), function2.get(direction, state, world, pos, entity)), defaultValue);
	}

	/**
	 * adds functions for {@link BlockProvider} and {@link BlockEntityProvider}
	 */
	public WorldAccess<T> addWorldProviderFunctions() {
		this.andThen((WorldFunction.NoBlockEntity<T>) (direction, state, view, pos) -> {
			Block block = state.getBlock();
			if(block instanceof BlockProvider) {
				return ((BlockProvider) block).get(this, direction, state, view, pos);
			}
			return null;
		});
		this.andThen((direction, state, view, pos, entity) -> {
			if(entity instanceof BlockEntityProvider) {
				return ((BlockEntityProvider) entity).get(this, direction);
			}
			return null;
		});
		return this;
	}

	public WorldAccess<T> forBlock(Block block, WorldFunction<T> function) {
		if (this.blockTypes.add(block, function)) {
			this.add((WorldFunction.NoBlockEntity<T>) (direction, state, view, pos) -> this.blockTypes.get(state.getBlock())
			                                                                                          .get(direction, state, view, pos));
		}
		return this;
	}

	public WorldAccess<T> forBlockState(BlockState block, WorldFunction<T> function) {
		if (this.blockStateTypes.add(block, function)) {
			this.add((WorldFunction.NoBlockEntity<T>) (direction, state, view, pos) -> this.blockStateTypes.get(state)
			                                                                                               .get(direction, state, view, pos));
		}
		return this;
	}

	public WorldAccess<T> forBlockEntity(BlockEntityType<?> block, WorldFunction<T> function) {
		if (this.blockEntityTypes.add(block, function)) {
			this.add((direction, state, view, pos, entity) -> entity != null ? this.blockEntityTypes.get(entity.getType())
			                                                                                        .get(direction, state, view, pos) : null);
		}
		return this;
	}
}
