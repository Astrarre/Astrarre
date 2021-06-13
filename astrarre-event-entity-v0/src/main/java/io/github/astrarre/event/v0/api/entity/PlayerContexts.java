package io.github.astrarre.event.v0.api.entity;

import io.github.astrarre.event.v0.api.core.Contexts;
import io.github.astrarre.event.v0.api.core.SingleContextHolder;
import io.github.astrarre.util.v0.api.func.Copier;

import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerContexts {
	public static final SingleContextHolder<ServerPlayerEntity> INTERACT_BLOCK = SingleContextHolder.newInstance("InteractBlock");
	static {
		Contexts.copyOnBlockEvent(INTERACT_BLOCK, Copier.immutable());
	}
}
