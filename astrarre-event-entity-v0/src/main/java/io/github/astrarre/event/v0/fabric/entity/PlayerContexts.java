package io.github.astrarre.event.v0.fabric.entity;

import io.github.astrarre.event.v0.api.core.Contexts;
import io.github.astrarre.event.v0.api.core.SingleContextHolder;

import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerContexts {
	public static final SingleContextHolder<ServerPlayerEntity> INTERACT_BLOCK = SingleContextHolder.newInstance("InteractBlock");
	public static final SingleContextHolder<ServerPlayerEntity> INTERACT_ITEM = SingleContextHolder.newInstance("InteractItem");
	public static final SingleContextHolder<ServerPlayerEntity> BREAK_BLOCK = SingleContextHolder.newInstance("BreakBlock");
	static {
		Contexts.copyOnBlockEvent(INTERACT_BLOCK, s -> s);
		Contexts.copyOnScheduledTick(INTERACT_BLOCK, s -> s);
	}
}
