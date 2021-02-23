package io.github.astrarre.access.v0.api.func;

import java.util.function.BinaryOperator;

import io.github.astrarre.v0.block.BlockState;
import io.github.astrarre.v0.block.entity.BlockEntity;
import io.github.astrarre.v0.util.math.BlockPos;
import io.github.astrarre.v0.util.math.Direction;
import io.github.astrarre.v0.world.World;
import org.jetbrains.annotations.Nullable;

public interface WorldFunction<T> extends Returns<T> {
	WorldFunction.NoBlock<?> EMPTY = (direction, view, pos) -> null;
	static <T> WorldFunction<T> empty() {
		return (WorldFunction<T>) EMPTY;
	}

	/**
	 * @param state the BlockState at the given world and position
	 * @param entity if {@code state.getBlock().hasBlockEntity()} then it is expected BlockEntity was already queried for
	 */
	@Nullable
	T get(@Nullable Direction direction, BlockState state, World view, BlockPos pos, @Nullable BlockEntity entity);

	@Nullable
	default T get(@Nullable Direction direction, World view, BlockPos pos) {
		return this.get(direction, view.getBlockState(pos), view, pos);
	}

	@Nullable
	default T get(@Nullable Direction direction, BlockState state, World view, BlockPos pos) {
		if (state.getBlock().hasBlockEntity()) {
			return this.get(direction, state, view, pos, view.getBlockEntity(pos));
		}
		return this.get(direction, state, view, pos, null);
	}

	/**
	 *
	 * @return a new function that combines this and the passed function, and merges the return type
	 */
	default WorldFunction<T> andThen(WorldFunction<T> function, BinaryOperator<T> merger) {
		return new WorldFunction<T>() {
			@Override
			@Nullable
			public T get(@Nullable Direction direction, BlockState state, World view, BlockPos pos, @Nullable BlockEntity entity) {
				return merger.apply(WorldFunction.this.get(direction, state, view, pos, entity), function.get(direction, state, view, pos, entity));
			}

			@Override
			@Nullable
			public T get(@Nullable Direction direction, World view, BlockPos pos) {
				return merger.apply(WorldFunction.this.get(direction, view, pos), function.get(direction, view, pos));
			}

			@Override
			@Nullable
			public T get(@Nullable Direction direction, BlockState state, World view, BlockPos pos) {
				return merger.apply(WorldFunction.this.get(direction, state, view, pos), function.get(direction, state, view, pos));
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
			public T get(@Nullable Direction direction, BlockState state, World view, BlockPos pos, @Nullable BlockEntity entity) {
				T obj = WorldFunction.this.get(direction, state, view, pos, entity);
				if (obj != null) {
					return obj;
				}

				return function.get(direction, state, view, pos, entity);
			}

			@Override
			@Nullable
			public T get(@Nullable Direction direction, World view, BlockPos pos) {
				T obj = WorldFunction.this.get(direction, view, pos);
				if (obj != null) {
					return obj;
				}

				return function.get(direction, view, pos);
			}

			@Override
			@Nullable
			public T get(@Nullable Direction direction, BlockState state, World view, BlockPos pos) {
				T obj = WorldFunction.this.get(direction, state, view, pos);
				if (obj != null) {
					return obj;
				}

				return function.get(direction, state, view, pos);
			}
		};
	}

	interface NoBlock<T> extends WorldFunction<T> {
		@Override
		@Nullable
		default T get(@Nullable Direction direction, BlockState state, World view, BlockPos pos, @Nullable BlockEntity entity) {
			return this.get(direction, view, pos);
		}

		@Override
		@Nullable
		T get(@Nullable Direction direction, World view, BlockPos pos);

		@Override
		@Nullable
		default T get(@Nullable Direction direction, BlockState state, World view, BlockPos pos) {
			return this.get(direction, view, pos);
		}
	}

	interface NoBlockEntity<T> extends WorldFunction<T> {
		@Override
		@Nullable
		default T get(@Nullable Direction direction, BlockState state, World view, BlockPos pos, @Nullable BlockEntity entity) {
			return this.get(direction, state, view, pos);
		}

		@Override
		@Nullable
		default T get(@Nullable Direction direction, World view, BlockPos pos) {
			return this.get(direction, view.getBlockState(pos), view, pos);
		}

		@Override
		@Nullable
		T get(@Nullable Direction direction, BlockState state, World view, BlockPos pos);
	}

	interface NoBlockState<T> extends WorldFunction<T> {
		@Override
		@Nullable
		default T get(@Nullable Direction direction, BlockState state, World view, BlockPos pos, @Nullable BlockEntity entity) {
			return this.get(direction, view, pos, entity);
		}

		@Override
		@Nullable
		default T get(@Nullable Direction direction, World view, BlockPos pos) {
			return this.get(direction, view, pos, view.getBlockEntity(pos));
		}

		@Nullable
		T get(@Nullable Direction direction, World view, BlockPos pos, @Nullable BlockEntity entity);

		@Override
		@Nullable
		default T get(@Nullable Direction direction, BlockState state, World view, BlockPos pos) {
			if (state.getBlock().hasBlockEntity()) {
				return this.get(direction, view, pos, view.getBlockEntity(pos));
			}
			return this.get(direction, view, pos, null);
		}
	}
}