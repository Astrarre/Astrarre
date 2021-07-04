package io.github.astrarre.access.v0.fabric.func;

import java.util.function.BinaryOperator;
import java.util.function.Predicate;

import io.github.astrarre.access.internal.AndThenWorldFunction;
import io.github.astrarre.access.internal.MergedAndThenWorldFunction;
import io.github.astrarre.access.internal.SkippingWorldFunction;
import io.github.astrarre.access.v0.fabric.cache.BlockData;
import io.github.astrarre.util.v0.api.func.IterFunc;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public interface WorldFunction<T> extends BaseWorldFunction {
	NoBlock<?> EMPTY = (direction, world, pos) -> null;
	static <T> WorldFunction<T> empty() {
		return (WorldFunction<T>) EMPTY;
	}

	/**
	 * finds the first non-default, non-null value, else returns the default value
	 */
	static <T> IterFunc<WorldFunction<T>> skipIfNull(T defaultValue) {
		return skipIf(t -> t != null && t != defaultValue, defaultValue);
	}

	/**
	 * finds the first non-default, non-null value, else returns the default value
	 */
	static <T> IterFunc<WorldFunction<T>> skipIf(Predicate<T> predicate, T defaultValue) {
		return (functions) -> new SkippingWorldFunction<>(functions, predicate, defaultValue);
	}

	/**
	 * @see BaseWorldFunction#optimizeQuery(BaseWorldFunction, boolean, boolean)
	 */
	static <T> WorldFunction<T> optimizeQuery(WorldFunction<T> function, boolean needsBlockState, boolean needsBlockEntity) {
		if(needsBlockEntity && needsBlockState) return function;
		if(needsBlockEntity) return (NoBlockState<T>) (dir, world, pos, entity) -> function.get(dir, null, world, pos, entity);
		if(needsBlockState) return (NoBlockEntity<T>) (dir, state, world, pos) -> function.get(dir, state, world, pos, null);
		return (NoBlock<T>) (dir, world, pos) -> function.get(dir, null, world, pos, null);
	}

	@Nullable
	default T get(@Nullable Direction direction, BlockData data) {
		if(this.needsBlockState() && this.needsBlockEntity()) {
			return this.get(direction, data.getState(), data.getWorld(), data.getPos(), data.getEntity());
		} else if(this.needsBlockEntity()) {
			return this.get(direction, null, data.getWorld(), data.getPos(), data.getEntity());
		} else if(this.needsBlockState()) {
			return this.get(direction, data.getState(), data.getWorld(), data.getPos(), null);
		} else {
			return this.get(direction, null, data.getWorld(), data.getPos(), null);
		}
	}

	/**
	 * @param state the BlockState at the given world and position
	 * @param entity if {@link BlockState#hasBlockEntity()} then it is expected BlockEntity was already queried for
	 */
	@Nullable
	T get(@Nullable Direction direction, BlockState state, World world, BlockPos pos, @Nullable BlockEntity entity);

	@Nullable
	default T get(@Nullable Direction direction, World world, BlockPos pos) {
		return this.get(direction, world.getBlockState(pos), world, pos);
	}

	@Nullable
	default T get(@Nullable Direction direction, BlockState state, World world, BlockPos pos) {
		if (state.hasBlockEntity()) {
			return this.get(direction, state, world, pos, world.getBlockEntity(pos));
		}
		return this.get(direction, state, world, pos, null);
	}

	/**
	 * @return a new function that combines this and the passed function, and merges the return type
	 */
	default WorldFunction<T> andThen(WorldFunction<T> function, BinaryOperator<T> merger) {
		return new MergedAndThenWorldFunction<>(merger, this, function);
	}

	/**
	 * @return a new function that attempts to get the value from the passed function if this function returns `null`
	 */
	default WorldFunction<T> andThen(WorldFunction<T> function) {
		return new AndThenWorldFunction<>(this, function);
	}

	interface NoBlock<T> extends WorldFunction<T> {
		@Override
		@Nullable
		default T get(@Nullable Direction direction, BlockState state, World world, BlockPos pos, @Nullable BlockEntity entity) {
			return this.get(direction, world, pos);
		}

		@Override
		@Nullable
		T get(@Nullable Direction direction, World world, BlockPos pos);

		@Override
		@Nullable
		default T get(@Nullable Direction direction, BlockState state, World world, BlockPos pos) {
			return this.get(direction, world, pos);
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

	interface NoBlockEntity<T> extends WorldFunction<T> {
		@Override
		@Nullable
		default T get(@Nullable Direction direction, BlockState state, World world, BlockPos pos, @Nullable BlockEntity entity) {
			return this.get(direction, state, world, pos);
		}

		@Override
		@Nullable
		default T get(@Nullable Direction direction, World world, BlockPos pos) {
			return this.get(direction, world.getBlockState(pos), world, pos);
		}

		@Override
		@Nullable
		T get(@Nullable Direction direction, BlockState state, World world, BlockPos pos);

		@Override
		default boolean needsBlockState() {
			return true;
		}

		@Override
		default boolean needsBlockEntity() {
			return false;
		}
	}

	interface NoBlockState<T> extends WorldFunction<T> {
		@Override
		@Nullable
		default T get(@Nullable Direction direction, BlockState state, World world, BlockPos pos, @Nullable BlockEntity entity) {
			return this.get(direction, world, pos, entity);
		}

		@Override
		@Nullable
		default T get(@Nullable Direction direction, World world, BlockPos pos) {
			return this.get(direction, world, pos, world.getBlockEntity(pos));
		}

		@Nullable
		T get(@Nullable Direction direction, World view, BlockPos pos, @Nullable BlockEntity entity);

		@Override
		@Nullable
		default T get(@Nullable Direction direction, BlockState state, World world, BlockPos pos) {
			if (state.hasBlockEntity()) {
				return this.get(direction, world, pos, world.getBlockEntity(pos));
			}
			return this.get(direction, world, pos, null);
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