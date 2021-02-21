package io.github.astrarre.access.v0.api;

import com.google.common.collect.Iterators;
import io.github.astrarre.access.internal.util.MapFilter;
import io.github.astrarre.access.v0.api.func.IterFunc;
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
		this((functions) -> (direction, state, world, pos, entity) -> {
			for (WorldFunction<T> function : functions) {
				T val = function.get(direction, state, world, pos, entity);
				if (val != null) {
					return val;
				}
			}
			return null;
		});
	}

	public WorldAccess(IterFunc<WorldFunction<T>> iterFunc) {
		super(iterFunc);
		this.blockEntityTypes = new MapFilter<>(iterFunc);
		this.blockStateTypes = new MapFilter<>(iterFunc);
		this.blockTypes = new MapFilter<>(iterFunc);
	}

	public static <T> WorldAccess<T> newInstance(IterFunc<T> combiner) {
		return new WorldAccess<>((functions) -> (direction, state, world, pos, entity) -> combiner.combine(() -> Iterators.transform(functions.iterator(),
				input -> input.get(direction, state, world, pos, entity))));
	}


	private boolean addedProviderFunction;
	/**
	 * adds functions for {@link BlockProvider} and {@link BlockEntityProvider}
	 *
	 * (calling this multiple times will only register it once)
	 */
	public WorldAccess<T> addWorldProviderFunctions() {
		if(this.addedProviderFunction) return this;
		this.addedProviderFunction = true;
		this.andThen((WorldFunction.NoBlockEntity<T>) (direction, state, view, pos) -> {
			Block block = state.getBlock();
			if (block instanceof BlockProvider) {
				return ((BlockProvider) block).get(this, direction, state, view, pos);
			}
			return null;
		});
		this.andThen((direction, state, view, pos, entity) -> {
			if (entity instanceof BlockEntityProvider) {
				return ((BlockEntityProvider) entity).get(this, direction);
			}
			return null;
		});
		return this;
	}

	public WorldAccess<T> forBlock(Block block, WorldFunction<T> function) {
		if (this.blockTypes.add(block, function)) {
			this.andThen((WorldFunction.NoBlockEntity<T>) (direction, state, view, pos) -> this.blockTypes.get(state.getBlock())
			                                                                                             .get(direction, state, view, pos));
		}
		return this;
	}

	public WorldAccess<T> forBlockState(BlockState block, WorldFunction<T> function) {
		if (this.blockStateTypes.add(block, function)) {
			this.andThen((WorldFunction.NoBlockEntity<T>) (direction, state, view, pos) -> this.blockStateTypes.get(state)
			                                                                                                  .get(direction, state, view, pos));
		}
		return this;
	}

	public WorldAccess<T> forBlockEntity(BlockEntityType<?> block, WorldFunction<T> function) {
		if (this.blockEntityTypes.add(block, function)) {
			this.andThen((direction, state, view, pos, entity) -> entity != null ? this.blockEntityTypes.get(entity.getType())
			                                                                                           .get(direction, state, view, pos) : null);
		}
		return this;
	}
}
