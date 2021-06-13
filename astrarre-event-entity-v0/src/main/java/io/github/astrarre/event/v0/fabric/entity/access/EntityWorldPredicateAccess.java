package io.github.astrarre.event.v0.fabric.entity.access;

import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.access.v0.fabric.helper.BlockEntityAccessHelper;
import io.github.astrarre.access.v0.fabric.helper.BlockStateAccessHelper;
import io.github.astrarre.access.v0.fabric.helper.EntityAccessHelper;
import io.github.astrarre.event.v0.fabric.entity.func.EntityWorldPredicate;
import io.github.astrarre.util.v0.api.Id;
import org.jetbrains.annotations.NotNull;

import net.minecraft.entity.Entity;

public class EntityWorldPredicateAccess<E extends Entity> extends Access<EntityWorldPredicate<E>> {
	public final EntityAccessHelper<EntityWorldPredicate<E>> entityFilter;
	public final BlockEntityAccessHelper<EntityWorldPredicate<E>> blockEntityHelper;
	public final BlockStateAccessHelper<EntityWorldPredicate<E>> blockStateHelper;

	public EntityWorldPredicateAccess(Id id) {
		super(id, EntityWorldPredicate.skipIfTrue());
		this.entityFilter = new EntityAccessHelper<>(
				this,
				f -> (e, world, pos, state, entity) -> f.apply(e).canDoAction(e, world, pos, state, entity),
				(e, world, pos, state, entity) -> true);
		this.blockEntityHelper = new BlockEntityAccessHelper<>(
				this,
				f -> (e, world, pos, state, entity) -> f.apply(entity).canDoAction(e, world, pos, state, entity),
				(e, world, pos, state, entity) -> true);
		this.blockStateHelper = new BlockStateAccessHelper<>(
				this,
				f -> (e, world, pos, state, entity) -> f.apply(state).canDoAction(e, world, pos, state, entity),
				(e, world, pos, state, entity) -> true);
	}

	/**
	 * state and blockEntity may be null
	 */
	@Override
	public @NotNull EntityWorldPredicate<E> get() {
		return super.get();
	}
}
