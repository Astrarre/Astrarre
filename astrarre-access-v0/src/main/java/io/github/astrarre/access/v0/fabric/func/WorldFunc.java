package io.github.astrarre.access.v0.fabric.func;

import java.util.function.BinaryOperator;
import java.util.function.Predicate;

import io.github.astrarre.access.internal.world_func.AndThenWorldFunc;
import io.github.astrarre.access.internal.world_func.MergedAndThenWorldFunc;
import io.github.astrarre.access.internal.world_func.SkippingWorldFunc;
import io.github.astrarre.access.v0.fabric.cache.BlockData;
import io.github.astrarre.util.v0.api.func.IterFunc;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * This function is meant to be curried https://en.wikipedia.org/wiki/Currying.
 * Eg.
 * <code>
 *     // equivalent to WorldFunction
 *     WorldFunc<Function<Direction, T>> worldFunction = (state, world, pos, entity) -> (direction) -> {...};
 * </code>
 */
public interface WorldFunc<T> extends BaseWorldFunction {
	WorldFunc.NoBlock<?> EMPTY = (world, pos) -> null;
	static <T> WorldFunc<T> empty() {
		return (WorldFunc<T>) EMPTY;
	}

	/**
	 * finds the first non-default, non-null value, else returns the default value
	 */
	static <T> IterFunc<WorldFunc<T>> skipIfNull(T defaultValue) {
		return skipIf(t -> t != null && t != defaultValue, defaultValue);
	}

	/**
	 * finds the first non-default, non-null value, else returns the default value
	 */
	static <T> IterFunc<WorldFunc<T>> skipIf(Predicate<T> predicate, T defaultValue) {
		return (functions) -> new SkippingWorldFunc<>(functions, predicate, defaultValue);
	}

	/**
	 * @see BaseWorldFunction#optimizeQuery(BaseWorldFunction, boolean, boolean)
	 */
	static <T> WorldFunc<T> optimizeQuery(WorldFunc<T> function, boolean needsBlockState, boolean needsBlockEntity, boolean curried) {
		if(needsBlockEntity && needsBlockState) return function;
		if(needsBlockEntity) return (WorldFunc.NoBlockState<T>) (world, pos, entity) -> function.get(null, world, pos, entity);
		if(needsBlockState) return (WorldFunc.NoBlockEntity<T>) function::get;
		return (WorldFunc.NoBlock<T>) (world, pos) -> function.get(null, world, pos, null);
	}

	@Nullable
	default T get(BlockData data) {
		if(this.needsBlockState() && this.needsBlockEntity()) {
			return this.get(data.getState(), data.getWorld(), data.getPos(), data.getEntity());
		} else if(this.needsBlockEntity()) {
			return this.get(null, data.getWorld(), data.getPos(), data.getEntity());
		} else if(this.needsBlockState()) {
			return this.get(data.getState(), data.getWorld(), data.getPos(), null);
		} else {
			return this.get(null, data.getWorld(), data.getPos(), null);
		}
	}

	/**
	 * @param state the BlockState at the given world and position
	 * @param entity if {@link BlockState#hasBlockEntity()} then it is expected BlockEntity was already queried for
	 */
	@Nullable
	T get(BlockState state, World world, BlockPos pos, @Nullable BlockEntity entity);

	@Nullable
	default T get(World world, BlockPos pos) {
		return this.get(world.getBlockState(pos), world, pos);
	}

	@Nullable
	default T get(BlockState state, World world, BlockPos pos) {
		if (state.hasBlockEntity()) {
			return this.get(state, world, pos, world.getBlockEntity(pos));
		}
		return this.get(state, world, pos, null);
	}

	/**
	 * @return a new function that combines this and the passed function, and merges the return type
	 */
	default WorldFunc<T> andThen(WorldFunc<T> function, BinaryOperator<T> merger) {
		return new MergedAndThenWorldFunc<>(merger, this, function);
	}

	/**
	 * @return a new function that attempts to get the value from the passed function if this function returns `null`
	 */
	default WorldFunc<T> andThen(WorldFunc<T> function) {
		return new AndThenWorldFunc<>(this, function);
	}

	interface NoBlock<T> extends WorldFunc<T> {
		@Override
		@Nullable
		default T get(BlockState state, World world, BlockPos pos, @Nullable BlockEntity entity) {
			return this.get(world, pos);
		}

		@Override
		@Nullable
		T get(World world, BlockPos pos);

		@Override
		@Nullable
		default T get(BlockState state, World world, BlockPos pos) {
			return this.get(world, pos);
		}

		@Override
		default boolean needsBlockState() {
			return false;
		}

		@Override
		default boolean needsBlockEntity() {
			return false;
		}
	}

	interface NoBlockEntity<T> extends WorldFunc<T> {
		@Override
		@Nullable
		default T get(BlockState state, World world, BlockPos pos, @Nullable BlockEntity entity) {
			return this.get(state, world, pos);
		}

		@Override
		@Nullable
		default T get(World world, BlockPos pos) {
			return this.get(world.getBlockState(pos), world, pos);
		}

		@Override
		@Nullable
		T get(BlockState state, World world, BlockPos pos);

		@Override
		default boolean needsBlockState() {
			return true;
		}

		@Override
		default boolean needsBlockEntity() {
			return false;
		}
	}

	interface NoBlockState<T> extends WorldFunc<T> {
		@Override
		@Nullable
		default T get(BlockState state, World world, BlockPos pos, @Nullable BlockEntity entity) {
			return this.get(world, pos, entity);
		}

		@Override
		@Nullable
		default T get(World world, BlockPos pos) {
			return this.get(world, pos, world.getBlockEntity(pos));
		}

		@Nullable
		T get(World view, BlockPos pos, @Nullable BlockEntity entity);

		@Override
		@Nullable
		default T get(BlockState state, World world, BlockPos pos) {
			if (state.hasBlockEntity()) {
				return this.get(world, pos, world.getBlockEntity(pos));
			}
			return this.get(world, pos, null);
		}

		@Override
		default boolean needsBlockState() {
			return false;
		}

		@Override
		default boolean needsBlockEntity() {
			return true;
		}
	}
}
