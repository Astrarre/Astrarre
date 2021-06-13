package io.github.astrarre.event.v0.api.entity.access;

import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.access.v0.fabric.helper.BlockEntityAccessHelper;
import io.github.astrarre.access.v0.fabric.helper.BlockStateAccessHelper;
import io.github.astrarre.access.v0.fabric.helper.EntityAccessHelper;
import io.github.astrarre.event.v0.api.entity.func.EntityBlockPermissionPredicate;
import io.github.astrarre.util.v0.api.Id;

public class EntityBlockPermissionAccess extends Access<EntityBlockPermissionPredicate> {
	public final EntityAccessHelper<EntityBlockPermissionPredicate> entityFilter;
	public final BlockEntityAccessHelper<EntityBlockPermissionPredicate> blockEntityHelper;
	public final BlockStateAccessHelper<EntityBlockPermissionPredicate> blockStateHelper;

	public EntityBlockPermissionAccess(Id id) {
		super(id, EntityBlockPermissionPredicate.ITER_FUNC);
		this.entityFilter = new EntityAccessHelper<>(
				this,
				function -> (e, world, pos, state, entity) -> function.apply(e).canDoAction(e, world, pos, state, entity),
				(e, world, pos, state, entity) -> true);
		this.blockEntityHelper = new BlockEntityAccessHelper<>(
				this,
				function -> (e, world, pos, state, entity) -> function.apply(entity).canDoAction(e, world, pos, state, entity),
				(e, world, pos, state, entity) -> true);
		this.blockStateHelper = new BlockStateAccessHelper<>(
				this,
				function -> (e, world, pos, state, entity) -> function.apply(state).canDoAction(e, world, pos, state, entity),
				(e, world, pos, state, entity) -> true);
	}
}
