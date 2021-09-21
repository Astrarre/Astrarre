package io.github.astrarre.access.internal.world_func;

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

public record SkippingWorldFunc<T>(Iterable<WorldFunc<T>> functions, Predicate<T> predicate, WorldFunc<T> defaultValue) implements WorldFunc<T> {
	public T get(@Nullable BlockState state, World world, BlockPos pos, @Nullable BlockEntity entity, boolean hasBlockEntity) {
		for (WorldFunc<T> function : this.functions) {
			entity = BaseWorldFunction.queryBlockEntity(function, state, world, pos, entity, hasBlockEntity);
			hasBlockEntity = function.needsBlockEntity();
			state = BaseWorldFunction.queryBlockState(function, state, world, pos, entity);
			T val = function.get(state, world, pos, entity);
			if (this.predicate.test(val)) {
				return val;
			}
		}

		var func = this.defaultValue;
		entity = BaseWorldFunction.queryBlockEntity(func, state, world, pos, entity, hasBlockEntity);
		state = BaseWorldFunction.queryBlockState(func, state, world, pos, entity);
		return func.get(state, world, pos, entity);
	}

	@Override
	public @Nullable T get(BlockState state, World world, BlockPos pos, @Nullable BlockEntity entity) {
		return this.get(state, world, pos, entity, true);
	}

	@Override
	public @Nullable T get(World world, BlockPos pos) {
		return this.get(null, world, pos, null);
	}

	@Override
	public @Nullable T get(BlockState state, World world, BlockPos pos) {
		return this.get(state, world, pos, null, false);
	}
}
