package io.github.astrarre.event.v0.fabric.entity.func;

import io.github.astrarre.access.v0.fabric.func.BaseWorldFunction;
import io.github.astrarre.util.v0.api.func.IterFunc;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @see BaseWorldFunction#optimizeQuery(BaseWorldFunction, boolean, boolean)
 */
public interface EntityWorldPredicate<E extends Entity> extends BaseWorldFunction {
	/**
	 * functions that come from an access that use this iter func can pass null to state and block entity
	 */
	static <E extends Entity> IterFunc<EntityWorldPredicate<E>> skipIfTrue() {
		return Skipping::new;
	}

	/**
	 * @param state can be null if {@link BaseWorldFunction#needsBlockState()} is false
	 * @param blockEntity can be null if {@link BaseWorldFunction#needsBlockEntity()} is false
	 */
	boolean canDoAction(E entity, World world, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity);

	record Skipping<T extends Entity>(Iterable<EntityWorldPredicate<T>> functions) implements EntityWorldPredicate<T> {
		@Override
		public boolean canDoAction(T entity, World world, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity) {
			boolean hasBlockEntity = false;
			for (EntityWorldPredicate<T> function : this.functions) {
				blockEntity = BaseWorldFunction.queryBlockEntity(function, state, world, pos, blockEntity, hasBlockEntity);
				hasBlockEntity = function.needsBlockEntity();
				state = BaseWorldFunction.queryBlockState(function, state, world, pos, blockEntity);
				if (!function.canDoAction(entity, world, pos, state, blockEntity)) {
					return false;
				}
			}
			return true;
		}
	}
}
