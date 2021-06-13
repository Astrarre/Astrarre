package io.github.astrarre.event.v0.api.player;

import io.github.astrarre.event.internal.core.InternalContexts;
import io.github.astrarre.event.v0.api.core.SingleContextHolder;
import io.github.astrarre.util.v0.api.func.Copier;

import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerContexts {
	public static final SingleContextHolder<ServerPlayerEntity> INTERACT_BLOCK = SingleContextHolder.newInstance("InteractBlock");
	static {
		InternalContexts.SYNC.add(new InternalContexts.CopyEntry<>(INTERACT_BLOCK, Copier.immutable(), INTERACT_BLOCK));
	}
}
