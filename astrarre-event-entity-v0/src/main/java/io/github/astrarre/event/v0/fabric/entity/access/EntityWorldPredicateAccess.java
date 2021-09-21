package io.github.astrarre.event.v0.fabric.entity.access;

import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.access.v0.fabric.helper.BlockEntityAccessHelper;
import io.github.astrarre.access.v0.fabric.helper.BlockStateAccessHelper;
import io.github.astrarre.access.v0.fabric.helper.EntityAccessHelper;
import io.github.astrarre.event.v0.fabric.entity.func.EntityWorldPredicate;
import io.github.astrarre.util.v0.api.Id;
import org.jetbrains.annotations.NotNull;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;

public class EntityWorldPredicateAccess<E extends Entity> extends Access<EntityWorldPredicate<E>> {
	public final EntityAccessHelper<EntityWorldPredicate<E>> entityFilter;
	public final BlockEntityAccessHelper<EntityWorldPredicate<E>> blockEntityHelper;
	public final BlockStateAccessHelper<EntityWorldPredicate<E>> blockStateHelper;

	public EntityWorldPredicateAccess(Id id) {
		super(id, EntityWorldPredicate.skipIfTrue());
		EntityWorldPredicate<E> predicate = (e, world, pos, state, entity) -> true;
		this.entityFilter = new EntityAccessHelper<>(this.funcFilter_(Entity.class, predicate));
		this.blockEntityHelper = new BlockEntityAccessHelper<>(this.funcFilter_(BlockEntity.class, predicate));
		this.blockStateHelper = new BlockStateAccessHelper<>(this.funcFilter_(BlockState.class, predicate));
	}

	/**
	 * state and blockEntity may be null
	 */
	@Override
	public @NotNull EntityWorldPredicate<E> get() {
		return super.get();
	}
}
