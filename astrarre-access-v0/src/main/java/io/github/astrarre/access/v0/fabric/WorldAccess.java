package io.github.astrarre.access.v0.fabric;

import java.util.Objects;

import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.reflect.TypeToken;
import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.access.v0.api.FunctionAccess;
import io.github.astrarre.access.v0.fabric.func.WorldFunction;
import io.github.astrarre.access.v0.fabric.helper.BlockEntityAccessHelper;
import io.github.astrarre.access.v0.fabric.helper.BlockStateAccessHelper;
import io.github.astrarre.access.v0.fabric.provider.BlockEntityProvider;
import io.github.astrarre.access.v0.fabric.provider.BlockProvider;
import io.github.astrarre.util.v0.api.Id;
import io.github.astrarre.util.v0.api.func.IterFunc;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.Box;

public class WorldAccess<T> extends Access<WorldFunction<T>> {
	private final BlockEntityAccessHelper<WorldFunction<T>> blockEntityHelper;
	private final BlockStateAccessHelper<WorldFunction<T>> blockStateHelper;
	private boolean addedProviderFunction, addedBlockEntityInstanceOfFunction;

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
		this.blockEntityHelper = new BlockEntityAccessHelper<>(comb, function -> {
			this.andThen((direction, state, world, pos, entity) -> {
				if (entity == null) {
					return null;
				}
				var f = function.apply(entity);
				return f != null ? f.get(direction, state, world, pos, entity) : null;
			});
		});
		this.blockStateHelper = new BlockStateAccessHelper<>(comb, function -> {
			this.andThen((direction, state, world, pos, entity) -> {
				var f = function.apply(state);
				return f != null ? f.get(direction, state, world, pos, entity) : null;
			});
		});
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
		this.getBlockStateHelper().getBlock().getBlock().forGenericProvider(this);
		this.getBlockStateHelper().getBlock().getFluid().forGenericProvider(this);
		this.getBlockEntityHelper().getBlockEntity().forGenericProvider(this);
		return this;
	}

	/**
	 * adds a function for if blockentity instanceof T, return blockentity
	 */
	public WorldAccess<T> addBlockEntityInstanceOfFunction(TypeToken<T> type) {
		if (this.addedBlockEntityInstanceOfFunction) {
			return this;
		}
		this.addedBlockEntityInstanceOfFunction = true;
		this.andThen((direction, state, world, pos, entity) -> {
			if (entity != null && type.isSupertypeOf(entity.getClass())) {
				return (T) entity;
			}
			return null;
		});
		return this;
	}

	/**
	 * The block advanced filtering helper. It is recommended you use these for performance's sake
	 */
	public BlockStateAccessHelper<WorldFunction<T>> getBlockStateHelper() {
		return this.blockStateHelper;
	}

	/**
	 * The block entity advanced filtering helper. It is recommended you use these for performance's sake
	 */
	public BlockEntityAccessHelper<WorldFunction<T>> getBlockEntityHelper() {
		return this.blockEntityHelper;
	}

	/**
	 * Utility method and example on how to use helpers
	 * @see #getBlockStateHelper()
	 */
	public WorldAccess<T> forBlockState(BlockState block, WorldFunction<T> function) {
		this.getBlockStateHelper().getBlockstate().forInstanceWeak(block, function);
		return this;
	}

	/**
	 * Utility method and example on how to use helpers
	 * @see #getBlockStateHelper()
	 */
	public WorldAccess<T> forBlock(Block block, WorldFunction<T> function) {
		this.getBlockStateHelper().getBlock().getBlock().forInstanceWeak(block, function);
		return this;
	}

	/**
	 * Utility method and example on how to use helpers
	 * @see #getBlockEntityHelper()
	 */
	public WorldAccess<T> forBlockEntity(BlockEntityType<?> block, WorldFunction<T> function) {
		this.getBlockEntityHelper().getBlockEntityType().forInstanceWeak(block, function);
		return this;
	}

	/**
	 * @param combiner if there are more than one valid entities within the same block, the values should be combined
	 */
	public WorldAccess<T> dependsOn(EntityAccess<T> entity, IterFunc<T> combiner) {
		super.dependsOn(entity,
				function -> (WorldFunction.NoBlock) (d, w, p) -> combiner.combine(Iterables.filter(Iterables.transform(w.getOtherEntities(null,
						new Box(p)), e -> function.get(d, e)), Objects::nonNull)));
		return this;
	}

	public static <T> WorldAccess<T> newInstance(Id id, IterFunc<T> combiner) {
		return new WorldAccess<>(id,
				(functions) -> (direction, state, world, pos, entity) -> combiner.combine(() -> Iterators.transform(functions.iterator(),
						input -> input.get(direction, state, world, pos, entity))));
	}
}
