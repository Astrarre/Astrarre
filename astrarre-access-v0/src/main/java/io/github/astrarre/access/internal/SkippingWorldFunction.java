package io.github.astrarre.access.internal;

import java.util.function.Predicate;

import io.github.astrarre.access.v0.fabric.func.WorldFunction;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class SkippingWorldFunction<T> implements WorldFunction<T> {
	private final Iterable<WorldFunction<T>> functions;
	private final Predicate<T> predicate;
	private final T defaultValue;

	public SkippingWorldFunction(Iterable<WorldFunction<T>> functions, Predicate<T> predicate, T defaultValue) {
		this.functions = functions;
		this.predicate = predicate;
		this.defaultValue = defaultValue;
	}

	@Override
	public @Nullable T get(@Nullable Direction direction, BlockState state, World world, BlockPos pos, @Nullable BlockEntity entity) {
		for (WorldFunction<T> function : this.functions) {
			T val = function.get(direction, state, world, pos, entity);
			if (this.predicate.test(val)) {
				return val;
			}
		}
		return this.defaultValue;
	}

	@Override
	public @Nullable T get(@Nullable Direction direction, World world, BlockPos pos) {
		BlockState state = null;
		BlockEntity entity = null;
		boolean askEntity = true;
		for (WorldFunction<T> function : this.functions) {
			T val;
			if (function instanceof NoBlock) {
				val = function.get(direction, world, pos);
			} else if (function instanceof NoBlockEntity) {
				if (state == null) {
					if (entity == null) {
						state = world.getBlockState(pos);
						askEntity = state.hasBlockEntity();
					} else {
						state = entity.getCachedState();
					}
				}
				val = function.get(direction, state, world, pos);
			} else if (function instanceof NoBlockState) {
				if (entity == null && askEntity) {
					entity = world.getBlockEntity(pos);
					askEntity = false;
				}
				val = ((NoBlockState<T>) function).get(direction, world, pos, entity);
			} else {
				if (entity == null && askEntity) {
					entity = world.getBlockEntity(pos);
					askEntity = false;
				}
				if (state == null) {
					if (entity == null) {
						state = world.getBlockState(pos);
						askEntity = state.hasBlockEntity();
					} else {
						state = entity.getCachedState();
					}
				}
				val = function.get(direction, state, world, pos, entity);
			}
			if (this.predicate.test(val)) {
				return val;
			}
		}
		return this.defaultValue;
	}

	@Override
	public @Nullable T get(@Nullable Direction direction, BlockState state, World world, BlockPos pos) {
		BlockEntity entity = null;
		boolean askEntity = true;
		for (WorldFunction<T> function : this.functions) {
			T val;
			if (function instanceof NoBlock) {
				val = function.get(direction, world, pos);
			} else if (function instanceof NoBlockEntity) {
				val = function.get(direction, state, world, pos);
			} else {
				if (entity == null && askEntity) {
					entity = world.getBlockEntity(pos);
					askEntity = false;
				}
				val = function.get(direction, state, world, pos, entity);
			}
			if (this.predicate.test(val)) {
				return val;
			}
		}
		return this.defaultValue;
	}
}
