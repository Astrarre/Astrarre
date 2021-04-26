package io.github.astrarre.access.v0.fabric;

import java.util.Objects;

import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import io.github.astrarre.access.internal.MapFilter;
import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.access.v0.api.FunctionAccess;
import io.github.astrarre.util.v0.api.func.IterFunc;
import io.github.astrarre.access.v0.fabric.func.WorldFunction;
import io.github.astrarre.access.v0.fabric.provider.BlockEntityProvider;
import io.github.astrarre.access.v0.fabric.provider.BlockProvider;
import io.github.astrarre.util.v0.api.Id;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.Box;

public class WorldAccess<T> extends Access<WorldFunction<T>> {
	private final MapFilter<BlockEntityType<?>, WorldFunction<T>> blockEntityTypes;
	private final MapFilter<BlockState, WorldFunction<T>> blockStateTypes;
	private final MapFilter<Block, WorldFunction<T>> blockTypes;
	private boolean addedProviderFunction;

	public WorldAccess(Id id) {
		this(id, (T) null);
	}

	/**
	 * docs for each of the constructors are the same from FunctionAccess
	 *
	 * @see FunctionAccess
	 */
	public WorldAccess(Id id, T defaultValue) {
		this(id, WorldFunction.skipIfNull(defaultValue));
	}

	public WorldAccess(Id id, IterFunc<WorldFunction<T>> iterFunc) {
		super(id, iterFunc);
		IterFunc<WorldFunction<T>> comb = WorldFunction.skipIfNull(null);
		this.blockEntityTypes = new MapFilter<>(comb);
		this.blockStateTypes = new MapFilter<>(comb);
		this.blockTypes = new MapFilter<>(comb);
	}

	public static <T> WorldAccess<T> newInstance(Id id, IterFunc<T> combiner) {
		return new WorldAccess<>(id, (functions) -> (direction, state, world, pos, entity) -> combiner.combine(() -> Iterators.transform(functions.iterator(),
				input -> input.get(direction, state, world, pos, entity))));
	}

	/**
	 * adds functions for {@link BlockProvider} and {@link BlockEntityProvider}
	 *
	 * (calling this multiple times will only register it once)
	 */
	public WorldAccess<T> addWorldProviderFunctions() {
		if (this.addedProviderFunction) {
			return this;
		}
		this.addedProviderFunction = true;
		this.andThen((WorldFunction.NoBlockEntity<T>) (direction, state, view, pos) -> {
			Block block = state.getBlock();
			if (block instanceof BlockProvider) {
				return (T) ((BlockProvider) block).get(this, direction, state, view, pos);
			}
			return null;
		});
		this.andThen((direction, state, view, pos, entity) -> {
			if (entity instanceof BlockEntityProvider) {
				return (T) ((BlockEntityProvider) entity).get(this, direction);
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

	/**
	 * @param combiner if there are more than one valid entities within the same block, the values should be combined
	 */
	public WorldAccess<T> dependsOn(EntityAccess<T> entity, IterFunc<T> combiner) {
		super.dependsOn(
				entity,
				function -> (WorldFunction.NoBlock) (d, w, p) -> combiner.combine(Iterables.filter(Iterables.transform(w.getOtherEntities(
						null,
						new Box(p)), e -> function.get(d, e)), Objects::nonNull)));
		return this;
	}
}
