package io.github.astrarre.event.v0.fabric.entity;

import io.github.astrarre.event.v0.fabric.entity.access.EntityWorldPredicateAccess;
import io.github.astrarre.util.v0.api.Id;

import net.minecraft.entity.player.PlayerEntity;

public interface EntityPermissions {
	EntityWorldPredicateAccess<PlayerEntity> BREAK_BLOCK = new EntityWorldPredicateAccess<>(Id.create("astrarre-event-entity-v0", "break_block"));
	EntityWorldPredicateAccess<PlayerEntity> PLACE_BLOCK = new EntityWorldPredicateAccess<>(Id.create("astrarre-event-entity-v0", "place_block"));
	EntityWorldPredicateAccess<PlayerEntity> INTERACT_BLOCK = new EntityWorldPredicateAccess<>(Id.create("astrarre-event-entity-v0", "interact_block"));
}
