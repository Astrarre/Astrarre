package io.github.astrarre.access.internal.world_function;

import java.util.function.Predicate;

import io.github.astrarre.access.v0.fabric.func.BaseWorldFunction;
import io.github.astrarre.access.v0.fabric.func.WorldFunc;
import io.github.astrarre.access.v0.fabric.func.WorldFunction;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public record SkippingWorldFunction<T>(Iterable<WorldFunction<T>> functions, Predicate<T> predicate, WorldFunction<T> defaultValue) implements WorldFunction<T> {
	public T get(@Nullable Direction direction, @Nullable BlockState state, World world, BlockPos pos, @Nullable BlockEntity entity, boolean hasBlockEntity) {
		for (WorldFunction<T> function : this.functions) {
			entity = BaseWorldFunction.queryBlockEntity(function, state, world, pos, entity, hasBlockEntity);
			hasBlockEntity = function.needsBlockEntity();
			state = BaseWorldFunction.queryBlockState(function, state, world, pos, entity);
			T val = function.get(direction, state, world, pos, entity);
			if (this.predicate.test(val)) {
				return val;
			}
		}

		WorldFunction<T> function = this.defaultValue;
		entity = BaseWorldFunction.queryBlockEntity(function, state, world, pos, entity, hasBlockEntity);
		state = BaseWorldFunction.queryBlockState(function, state, world, pos, entity);
		return function.get(direction, state, world, pos, entity);
	}

	@Override
	public @Nullable T get(@Nullable Direction direction, BlockState state, World world, BlockPos pos, @Nullable BlockEntity entity) {
		return this.get(direction, state, world, pos, entity, true);
	}

	@Override
	public @Nullable T get(@Nullable Direction direction, World world, BlockPos pos) {
		return this.get(direction, null, world, pos, null);
	}

	@Override
	public @Nullable T get(@Nullable Direction direction, BlockState state, World world, BlockPos pos) {
		return this.get(direction, state, world, pos, null, false);
	}
}
