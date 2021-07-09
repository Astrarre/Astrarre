package io.github.astrarre.access.internal.world_func;

import java.util.function.BinaryOperator;

import io.github.astrarre.access.v0.fabric.func.WorldFunc;
import io.github.astrarre.access.v0.fabric.func.WorldFunction;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public record MergedAndThenWorldFunc<T>(BinaryOperator<T> merger, WorldFunc<T> current, WorldFunc<T> function) implements WorldFunc<T> {
	@Override
	@Nullable
	public T get(BlockState state, World world, BlockPos pos, @Nullable BlockEntity entity) {
		return this.merger.apply(this.current.get(state, world, pos, entity), this.function.get(state, world, pos, entity));
	}

	@Override
	@Nullable
	public T get(World world, BlockPos pos) {
		return this.merger.apply(this.current.get(world, pos), this.function.get(world, pos));
	}

	@Override
	@Nullable
	public T get(BlockState state, World world, BlockPos pos) {
		return this.merger.apply(this.current.get(state, world, pos), this.function.get(state, world, pos));
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
