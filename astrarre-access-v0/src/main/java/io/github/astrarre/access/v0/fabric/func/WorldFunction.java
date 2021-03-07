package io.github.astrarre.access.v0.fabric.func;

import java.util.function.BinaryOperator;

import io.github.astrarre.access.v0.api.func.Returns;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public interface WorldFunction<T> extends Returns<T> {
	NoBlock<?> EMPTY = (direction, world, pos) -> null;
	static <T> WorldFunction<T> empty() {
		return (WorldFunction<T>) EMPTY;
	}

	/**
	 * @param state the BlockState at the given world and position
	 * @param entity if {@code state.getBlock().hasBlockEntity()} then it is expected BlockEntity was already queried for
	 */
	@Nullable
	T get(@Nullable Direction direction, BlockState state, World world, BlockPos pos, @Nullable BlockEntity entity);

	@Nullable
	default T get(@Nullable Direction direction, World world, BlockPos pos) {
		return this.get(direction, world.getBlockState(pos), world, pos);
	}

	@Nullable
	default T get(@Nullable Direction direction, BlockState state, World world, BlockPos pos) {
		if (state.getBlock().hasBlockEntity()) {
			return this.get(direction, state, world, pos, world.getBlockEntity(pos));
		}
		return this.get(direction, state, world, pos, null);
	}

	/**
	 *
	 * @return a new function that combines this and the passed function, and merges the return type
	 */
	default WorldFunction<T> andThen(WorldFunction<T> function, BinaryOperator<T> merger) {
		return new WorldFunction<T>() {
			@Override
			@Nullable
			public T get(@Nullable Direction direction, BlockState state, World world, BlockPos pos, @Nullable BlockEntity entity) {
				return merger.apply(WorldFunction.this.get(direction, state, world, pos, entity), function.get(direction, state, world, pos, entity));
			}

			@Override
			@Nullable
			public T get(@Nullable Direction direction, World world, BlockPos pos) {
				return merger.apply(WorldFunction.this.get(direction, world, pos), function.get(direction, world, pos));
			}

			@Override
			@Nullable
			public T get(@Nullable Direction direction, BlockState state, World world, BlockPos pos) {
				return merger.apply(WorldFunction.this.get(direction, state, world, pos), function.get(direction, state, world, pos));
			}
		};
	}

	/**
	 * @return a new function that attempts to get the value from the passed function if this function returns `null`
	 */
	default WorldFunction<T> andThen(WorldFunction<T> function) {
		return new WorldFunction<T>() {
			@Override
			@Nullable
			public T get(@Nullable Direction direction, BlockState state, World world, BlockPos pos, @Nullable BlockEntity entity) {
				T obj = WorldFunction.this.get(direction, state, world, pos, entity);
				if (obj != null) {
					return obj;
				}

				return function.get(direction, state, world, pos, entity);
			}

			@Override
			@Nullable
			public T get(@Nullable Direction direction, World world, BlockPos pos) {
				T obj = WorldFunction.this.get(direction, world, pos);
				if (obj != null) {
					return obj;
				}

				return function.get(direction, world, pos);
			}

			@Override
			@Nullable
			public T get(@Nullable Direction direction, BlockState state, World world, BlockPos pos) {
				T obj = WorldFunction.this.get(direction, state, world, pos);
				if (obj != null) {
					return obj;
				}

				return function.get(direction, state, world, pos);
			}
		};
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
			if (state.getBlock().hasBlockEntity()) {
				return this.get(direction, world, pos, world.getBlockEntity(pos));
			}
			return this.get(direction, world, pos, null);
		}
	}
}