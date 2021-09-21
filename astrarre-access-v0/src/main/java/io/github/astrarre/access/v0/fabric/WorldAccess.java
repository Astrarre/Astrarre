package io.github.astrarre.access.v0.fabric;

import java.util.Objects;

import com.google.common.collect.Iterables;
import com.google.common.reflect.TypeToken;
import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.access.v0.api.FunctionAccess;
import io.github.astrarre.access.v0.fabric.func.WorldFunction;
import io.github.astrarre.access.v0.fabric.helper.BlockEntityAccessHelper;
import io.github.astrarre.access.v0.fabric.helper.BlockStateAccessHelper;
import io.github.astrarre.access.v0.fabric.provider.BlockEntityProvider;
import io.github.astrarre.access.v0.fabric.provider.BlockProvider;
import io.github.astrarre.util.v0.api.Id;
import io.github.astrarre.util.v0.api.func.ArrayFunc;
import io.github.astrarre.util.v0.api.func.IterFunc;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
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
	 * @see FunctionAccess
	 */
	public WorldAccess(Id id, T defaultValue) {
		this(id, WorldFunction.skipIfNull(defaultValue), (WorldFunction.NoBlock<T>) (d, w, p) -> null);
	}

	public WorldAccess(Id id, ArrayFunc<WorldFunction<T>> iterFunc) {
		this(id, iterFunc, iterFunc.empty());
	}

	public WorldAccess(Id id, ArrayFunc<WorldFunction<T>> iterFunc, T defaultValue) {
		this(id, iterFunc, (WorldFunction.NoBlock<T>) (d, w, p) -> defaultValue);
	}

	public WorldAccess(Id id, ArrayFunc<WorldFunction<T>> iterFunc, WorldFunction<T> function) {
		super(id, iterFunc);
		this.blockEntityHelper = new BlockEntityAccessHelper<>(this.funcFilter_(BlockEntity.class, function));
		this.blockStateHelper = new BlockStateAccessHelper<>(this.funcFilter_(BlockState.class, function));
	}

	/**
	 * adds functions for {@link BlockProvider} and {@link BlockEntityProvider}
	 *
	 * (calling this multiple times will only register it once)
	 */
	public WorldAccess<T> addWorldProviderFunctions() {
		if(this.addedProviderFunction) {
			return this;
		}
		this.addedProviderFunction = true;
		this.andThen((WorldFunction.NoBlockEntity<T>) (direction, state, view, pos) -> {
			Block block = state.getBlock();
			if(block instanceof BlockProvider) {
				return (T) ((BlockProvider) block).get(this, direction, state, view, pos);
			}
			return null;
		});
		this.andThen((direction, state, view, pos, entity) -> {
			if(entity instanceof BlockEntityProvider) {
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
		if(this.addedBlockEntityInstanceOfFunction) {
			return this;
		}
		this.addedBlockEntityInstanceOfFunction = true;
		this.andThen((direction, state, world, pos, entity) -> {
			if(entity != null && type.isSupertypeOf(entity.getClass())) {
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
	 *
	 * @see #getBlockStateHelper()
	 */
	public WorldAccess<T> forBlockState(BlockState block, WorldFunction<T> function) {
		this.getBlockStateHelper().getBlockstate().forInstanceWeak(block, function);
		return this;
	}

	/**
	 * Utility method and example on how to use helpers
	 *
	 * @see #getBlockStateHelper()
	 */
	public WorldAccess<T> forBlock(Block block, WorldFunction<T> function) {
		this.getBlockStateHelper().getBlock().getBlock().forInstanceWeak(block, function);
		return this;
	}

	/**
	 * Utility method and example on how to use helpers
	 *
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
		super.dependsOn(entity, function -> (WorldFunction.NoBlock<T>) (d, w, p) -> {
			var entities = w.getOtherEntities(null, new Box(p));
			return combiner.combine(Iterables.filter(Iterables.transform(entities, e -> function.get(d, e)), Objects::nonNull));
		});
		return this;
	}

	public static <T> WorldAccess<T> newInstance(Id id, IterFunc<T> combiner) {
		return new WorldAccess<>(id, functions -> (d, s, w, p, e) -> transform(functions, f -> f.get(d, s, w, p, e), combiner));
	}
}
