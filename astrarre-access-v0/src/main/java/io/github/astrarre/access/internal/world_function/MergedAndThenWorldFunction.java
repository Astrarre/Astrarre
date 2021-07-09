package io.github.astrarre.access.internal.world_function;

import java.util.function.BinaryOperator;

import io.github.astrarre.access.v0.fabric.func.WorldFunction;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public record MergedAndThenWorldFunction<T>(BinaryOperator<T> merger, WorldFunction<T> current, WorldFunction<T> function) implements WorldFunction<T> {
	@Override
	@Nullable
	public T get(@Nullable Direction direction, BlockState state, World world, BlockPos pos, @Nullable BlockEntity entity) {
		return this.merger.apply(this.current.get(direction, state, world, pos, entity), this.function.get(direction, state, world, pos, entity));
	}

	@Override
	@Nullable
	public T get(@Nullable Direction direction, World world, BlockPos pos) {
		return this.merger.apply(this.current.get(direction, world, pos), this.function.get(direction, world, pos));
	}

	@Override
	@Nullable
	public T get(@Nullable Direction direction, BlockState state, World world, BlockPos pos) {
		return this.merger.apply(this.current.get(direction, state, world, pos), this.function.get(direction, state, world, pos));
	}

	@Override
	public boolean needsBlockEntity() {
		return this.current.needsBlockEntity() || this.function.needsBlockEntity();
	}

	@Override
	public boolean needsBlockState() {
		return this.current.needsBlockState() || this.function.needsBlockState();
	}
}
