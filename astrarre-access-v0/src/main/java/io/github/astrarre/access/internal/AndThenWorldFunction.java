package io.github.astrarre.access.internal;

import io.github.astrarre.access.v0.fabric.func.WorldFunction;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class AndThenWorldFunction<T> implements WorldFunction<T> {
	private final WorldFunction<T> current, function;

	public AndThenWorldFunction(WorldFunction<T> current, WorldFunction<T> function) {
		this.current = current;
		this.function = function;
	}

	@Override
	@Nullable
	public T get(@Nullable Direction direction, BlockState state, World world, BlockPos pos, @Nullable BlockEntity entity) {
		T obj = this.current.get(direction, state, world, pos, entity);
		if (obj != null) {
			return obj;
		}

		return this.function.get(direction, state, world, pos, entity);
	}

	@Override
	@Nullable
	public T get(@Nullable Direction direction, World world, BlockPos pos) {
		T obj = this.current.get(direction, world, pos);
		if (obj != null) {
			return obj;
		}

		return this.function.get(direction, world, pos);
	}

	@Override
	@Nullable
	public T get(@Nullable Direction direction, BlockState state, World world, BlockPos pos) {
		T obj = this.current.get(direction, state, world, pos);
		if (obj != null) {
			return obj;
		}

		return this.function.get(direction, state, world, pos);
	}
}
