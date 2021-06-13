package io.github.astrarre.event.v0.api.entity.func;

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
public interface EntityBlockPermissionPredicate extends BaseWorldFunction {
	IterFunc<EntityBlockPermissionPredicate> ITER_FUNC = Skipping::new;

	boolean canDoAction(Entity e, World world, BlockPos pos, BlockState state, @Nullable BlockEntity entity);

	record Skipping(Iterable<EntityBlockPermissionPredicate> functions) implements EntityBlockPermissionPredicate {
		@Override
		public boolean canDoAction(Entity e, World world, BlockPos pos, BlockState state, @Nullable BlockEntity entity) {
			boolean hasBlockEntity = false;
			for (EntityBlockPermissionPredicate function : this.functions) {
				entity = BaseWorldFunction.queryBlockEntity(function, state, world, pos, entity, hasBlockEntity);
				hasBlockEntity = function.needsBlockEntity();
				state = BaseWorldFunction.queryBlockState(function, state, world, pos, entity);
				if (!function.canDoAction(e, world, pos, state, entity)) {
					return false;
				}
			}
			return true;
		}
	}

}
