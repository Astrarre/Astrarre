package io.github.astrarre.event.v0.fabric.entity;

import io.github.astrarre.event.v0.api.core.ContextHolder;
import io.github.astrarre.event.v0.api.core.ContextView;
import io.github.astrarre.event.v0.api.core.Contexts;
import io.github.astrarre.event.v0.api.core.SingleContextHolder;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * some of these contexts may have outdated or invalid references.
 * For example, if a player places a block entity, logs of, and logs back on (or goes from end to overworld), the context will have a reference to the old player entity.
 */
public class EntityContexts {
	public static final ContextHolder<ServerPlayerEntity> INTERACT_BLOCK = ContextHolder.newInstance("InteractBlock");
	public static final ContextHolder<ServerPlayerEntity> INTERACT_ITEM = ContextHolder.newInstance("InteractItem");
	public static final ContextHolder<ServerPlayerEntity> BREAK_BLOCK = ContextHolder.newInstance("BreakBlock");
	public static final ContextHolder<Entity> TICK_ENTITY = ContextHolder.newInstance("TickEntity");
	public static final ContextHolder<Entity> MOVE_ENTITY = ContextHolder.newInstance("MoveEntity");

	public static final ContextView<ServerPlayerEntity> PLAYER_INTERACT = ContextView.combine(INTERACT_BLOCK, INTERACT_ITEM, BREAK_BLOCK);

	public static final ContextView<Entity> ENTITY = ContextView.combine(PLAYER_INTERACT, TICK_ENTITY, MOVE_ENTITY);

	static {
		Contexts.copyForAll(INTERACT_BLOCK, s -> s);
		Contexts.copyForAll(INTERACT_ITEM, s -> s);
		Contexts.copyForAll(BREAK_BLOCK, s -> s);
		Contexts.copyForAll(TICK_ENTITY, s -> s);
		Contexts.copyForAll(MOVE_ENTITY, s -> s);

	}
}
